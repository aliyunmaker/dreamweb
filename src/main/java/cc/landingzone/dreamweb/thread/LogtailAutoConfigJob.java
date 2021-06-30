package cc.landingzone.dreamweb.thread;

import cc.landingzone.dreamweb.common.EndpointConstants;
import cc.landingzone.dreamweb.utils.SlsUtils;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.oos.model.v20190601.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class LogtailAutoConfigJob implements Callable<String> {

    private static Logger logger = LoggerFactory.getLogger(LogtailAutoConfigJob.class);
    private static String LINE_BREAK = "<br>";
    private static String LINE_SEPARATED = "<hr>";

    private String accountId;
    private String action; // 安装 or 卸载
    private IAcsClient client;
    private CountDownLatch countDownLatch;
    private List<String> ecsInstanceIdList;
    private String masterUid;
    private StringBuilder result;
    private String region;
    private Boolean useVpc;

    public LogtailAutoConfigJob(String accountId, String action, IAcsClient client, CountDownLatch countDownLatch,
                                List<String> ecsInstanceIdList, String masterUid, String region, Boolean useVpc) {
        this.accountId = accountId;
        this.action = action;
        this.client = client;
        this.countDownLatch = countDownLatch;
        this.ecsInstanceIdList = ecsInstanceIdList;
        this.masterUid = masterUid;
        this.result = new StringBuilder();
        this.region = region;
        this.useVpc = useVpc;
    }

    @Override
    public String call() {
        String oosEndpoint = EndpointConstants.getOosEndpoint(region, useVpc);

        result.append("<b>start " + SlsUtils.drawWithColor(action) + " logtail on account: " + accountId + "</b>");
        result.append(LINE_BREAK);
        result.append("targets: ");
        result.append(LINE_BREAK);

        StringBuilder resourceIdsBuilder = new StringBuilder();
        for (String instanceId : ecsInstanceIdList) {
            resourceIdsBuilder.append("\"" + instanceId + "\",");
        }
        if (resourceIdsBuilder.length() > 0) {
            resourceIdsBuilder.deleteCharAt(resourceIdsBuilder.length() - 1);
        }

        String resourceIds = resourceIdsBuilder.toString();
        result.append("- ecs resource ids: " + resourceIds);
        result.append(LINE_BREAK);
        result.append("- region: " + region);
        result.append(LINE_BREAK);
        result.append(LINE_SEPARATED);

        try {
            // 1. 安装Logtail插件
            String logtailTemplate = "ACS-ECS-BulkyInstallLogAgent";
            String logtailParameter = "{\"action\":\"" + action + "\","
                + "\"rateControl\":{\"MaxErrors\":100,\"Concurrency\":1,\"Mode\":\"Concurrency\"},"
                + "\"regionId\":\"cn-hangzhou\","
                + "\"targets\":{\"Type\":\"ResourceIds\",\"ResourceIds\":[" + resourceIds + "],"
                + "\"regionId\":\"" + region + "\"}}";
            String executionId = executeTemplate(logtailTemplate, logtailParameter, oosEndpoint);
            result.append(SlsUtils.drawWithColor(action) + " logtail, executionId: " + executionId);
            result.append(LINE_BREAK);

            Boolean isFinished = false;
            while (!isFinished) {
                // 每三秒查询一次执行结果
                Thread.sleep(3000);

                String status = queryExecutionResult(client, executionId, oosEndpoint);
                if ("Success".equals(status)) {
                    isFinished = true;
                    result.append("* status: " + SlsUtils.drawWithColor(status));
                    result.append(LINE_BREAK);
                } else if ("Failed".equals(status) || "Cancelled".equals(status)) {
                    result.append("* status: " + SlsUtils.drawWithColor(status));
                    result.append(LINE_BREAK);
                    break;
                }
                logger.info("Thread{}: get execution status: {}", accountId, status);
            }

            // 2. 创建Logtail跨账号模板
            result.append(LINE_SEPARATED);
            result.append(SlsUtils.drawWithColor(action) + " crossAccounts logtail configuration");
            result.append(LINE_BREAK);

            String crossAccountsTemplate = "slsCrossAccountsTemplate";
            createCrossAccountsTemplate(crossAccountsTemplate, oosEndpoint);
            logger.info("Thread{}: create crossAccountsTemplate success", accountId);

            // 3. 执行模板
            String crossAccountParameters =
                "{\"rateControl\":{\"MaxErrors\":100,\"Concurrency\":1,\"Mode\":\"Concurrency\"}," +
                    "\"targets\":{\"Type\":\"ResourceIds\",\"ResourceIds\":[" + resourceIds + "],\"RegionId\":\""
                    + region + "\"}," +
                    "\"masterUid\":\"" + masterUid + "\"," +
                    "\"action\":\"" + action + "\"," +
                    "\"accountId\":\"" + accountId + "\"" +
                    "}";
            String configureExecutionId = executeTemplate(crossAccountsTemplate, crossAccountParameters, oosEndpoint);
            result.append("* execute " + crossAccountsTemplate + ", execution id: " + configureExecutionId);
            result.append(LINE_BREAK);
            result.append("* status: " + SlsUtils.drawWithColor("Success"));
            result.append(LINE_BREAK);

            logger.info("Thread{}: execute crossAccountsTemplate success", accountId);
            result.append("<b>finish " + SlsUtils.drawWithColor(action) + " logtail on account: " + accountId + "</b>");
            result.append(LINE_BREAK);
            result.append(LINE_SEPARATED);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.append(e.getMessage());
            result.append(LINE_BREAK);
        } finally {
            countDownLatch.countDown();
        }

        return result.toString();
    }

    /**
     * 在ECS上执行模板
     *
     * @param templateName 模板名称
     * @param parameters   参数
     * @return 执行Id
     * @throws ClientException
     */
    private String executeTemplate(String templateName, String parameters, String endpoint) throws ClientException {
        StartExecutionRequest request = new StartExecutionRequest();
        request.setTemplateName(templateName);
        request.setParameters(parameters);
        request.setSysEndpoint(endpoint);

        StartExecutionResponse response;
        String executionId = "";

        response = client.getAcsResponse(request);
        executionId = response.getExecution().getExecutionId();

        return executionId;
    }

    /**
     * 查询某个Execution的执行结果
     *
     * @param client      client连接
     * @param ExecutionId 执行id
     * @return 成功 or 等待 or 失败 or 取消
     * @throws ClientException
     */
    private String queryExecutionResult(IAcsClient client, String ExecutionId, String endpoint) throws ClientException {
        ListExecutionsRequest request = new ListExecutionsRequest();
        request.setExecutionId(ExecutionId);
        request.setSysEndpoint(endpoint);

        ListExecutionsResponse response = client.getAcsResponse(request);
        for (ListExecutionsResponse.Execution execution : response.getExecutions()) {
            String status = execution.getStatus();
            if ("Failed".equals(status) || "Cancelled".equals(status)) {
                result.append("execute failed, output: " + execution.getOutputs());
                result.append(LINE_BREAK);
                logger.error("ExecutionId: {}\nExecutionOutput: {}", ExecutionId, execution.getOutputs());
                return status;
            } else if (!"Success".equals(status)) {
                return "Waiting";
            }
        }

        for (ListExecutionsResponse.Execution execution : response.getExecutions()) {
            ListTaskExecutionsRequest taskRequest = new ListTaskExecutionsRequest();
            taskRequest.setExecutionId(execution.getExecutionId());
            ListTaskExecutionsResponse taskResponse = client.getAcsResponse(taskRequest);

            for (ListTaskExecutionsResponse.TaskExecution task : taskResponse.getTaskExecutions()) {
                result.append("* subTask: " + task.getTaskName() + " id: " + task.getTaskExecutionId());
                result.append(LINE_BREAK);
                result.append("* outputs: " + task.getOutputs());
                result.append(LINE_BREAK);
            }
        }

        return "Success";
    }

    /**
     * 创建模板
     *
     * @param templateName 创建模板名称
     * @throws ClientException
     */
    private void createCrossAccountsTemplate(String templateName, String endpoint) throws ClientException {
        if (getTemplateExistStatus(templateName, endpoint)) {
            result.append("* tips: template already exist");
            result.append(LINE_BREAK);
            return;
        }

        String crossAccountsTemplateContent = "{\n" +
            "  \"FormatVersion\": \"OOS-2019-06-01\",\n" +
            "  \"Description\": \"批量在ECS实例中执行命令\",\n" +
            "  \"Parameters\": {\n" +
            "    \"targets\": {\n" +
            "      \"Type\": \"Json\",\n" +
            "      \"Description\": \"ECS实例\",\n" +
            "      \"AssociationProperty\": \"Targets\",\n" +
            "      \"AssociationPropertyMetadata\": {\n" +
            "        \"ResourceType\": \"ALIYUN::ECS::Instance\",\n" +
            "        \"RegionId\": \"regionId\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"rateControl\": {\n" +
            "      \"Description\": \"任务执行的并发比率\",\n" +
            "      \"Type\": \"Json\",\n" +
            "      \"AssociationProperty\": \"RateControl\",\n" +
            "      \"Default\": {\n" +
            "        \"Mode\": \"Concurrency\",\n" +
            "        \"MaxErrors\": 0,\n" +
            "        \"Concurrency\": 10\n" +
            "      }\n" +
            "    },\n" +
            "    \"OOSAssumeRole\": {\n" +
            "      \"Description\": \"OOS扮演的RAM角色\",\n" +
            "      \"Type\": \"String\",\n" +
            "      \"Default\": \"OOSServiceRole\"\n" +
            "    },\n" +
            "    \"masterUid\": {\n" +
            "      \"Type\": \"String\",\n" +
            "      \"Description\": \"主账号uid\"\n" +
            "    },\n" +
            "    \"accountId\": {\n" +
            "      \"Type\": \"String\",\n" +
            "      \"Description\": \"当前账号id\"\n" +
            "    },\n" +
            "    \"action\": {\n" +
            "      \"Type\": \"String\",\n" +
            "      \"Description\": \"执行的操作\",\n" +
            "      \"AllowedValues\": [\n" +
            "        \"install\",\n" +
            "        \"uninstall\"\n" +
            "      ]\n" +
            "    }\n" +
            "  },\n" +
            "  \"RamRole\": \"{{ OOSAssumeRole }}\",\n" +
            "  \"Tasks\": [\n" +
            "    {\n" +
            "      \"Name\": \"getInstance\",\n" +
            "      \"Description\": \"获取ECS实例\",\n" +
            "      \"Action\": \"ACS::SelectTargets\",\n" +
            "      \"Properties\": {\n" +
            "        \"ResourceType\": \"ALIYUN::ECS::Instance\",\n" +
            "        \"Filters\": [\n" +
            "          \"{{ targets }}\"\n" +
            "        ]\n" +
            "      },\n" +
            "      \"Outputs\": {\n" +
            "        \"instanceIds\": {\n" +
            "          \"Type\": \"List\",\n" +
            "          \"ValueSelector\": \"Instances.Instance[].InstanceId\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"Name\": \"runCommand\",\n" +
            "      \"Action\": \"ACS::ECS::RunCommand\",\n" +
            "      \"Description\": \"执行云助手命令\",\n" +
            "      \"Properties\": {\n" +
            "        \"commandType\": \"RunShellScript\",\n" +
            "        \"instanceId\": \"{{ ACS::TaskLoopItem }}\",\n" +
            "        \"regionId\": \"{{ ACS::RegionId }}\",\n" +
            "        \"commandContent\": \"if [ \\\"{{action}}\\\" = \\\"install\\\" ]; then\\n  if [ ! -f "
            + "\\\"/etc/ilogtail/users/{{masterUid}}\\\" ]; then\\n    touch /etc/ilogtail/users/{{masterUid}}\\n  "
            + "fi\\n\\n  if [ ! -f \\\"/etc/ilogtail/user_defined_id\\\" ]; then\\n    touch "
            + "/etc/ilogtail/user_defined_id\\n  fi\\n  echo {{accountId}} >> /etc/ilogtail/user_defined_id\\nelse\\n"
            + "  if [ -f \\\"/etc/ilogtail/users/{{masterUid}}\\\" ]; then\\n    rm -f "
            + "/etc/ilogtail/users/{{masterUid}}\\n  fi\\n\\n  if [ -f \\\"/etc/ilogtail/user_defined_id\\\" ]; "
            + "then\\n    rm -f /etc/ilogtail/user_defined_id\\n  fi\\nfi\",\n"
            +
            "        \"workingDir\": \"\",\n" +
            "        \"windowsPasswordName\": \"\",\n" +
            "        \"enableParameter\": false,\n" +
            "        \"timeout\": 600,\n" +
            "        \"username\": \"\"\n" +
            "      },\n" +
            "      \"Loop\": {\n" +
            "        \"Items\": \"{{ getInstance.instanceIds }}\",\n" +
            "        \"RateControl\": \"{{ rateControl }}\",\n" +
            "        \"Outputs\": {\n" +
            "          \"invocationOutputs\": {\n" +
            "            \"AggregateType\": \"Fn::ListJoin\",\n" +
            "            \"AggregateField\": \"invocationOutput\"\n" +
            "          }\n" +
            "        }\n" +
            "      },\n" +
            "      \"Outputs\": {},\n" +
            "      \"OnError\": \"ACS::END\",\n" +
            "      \"OnSuccess\": \"ACS::NEXT\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"Outputs\": {\n" +
            "    \"invocationOutputs\": {\n" +
            "      \"Type\": \"List\",\n" +
            "      \"Value\": \"{{ runCommand.invocationOutputs }}\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

        CreateTemplateRequest request = new CreateTemplateRequest();
        request.setTemplateName(templateName);
        request.setContent(crossAccountsTemplateContent);
        request.setSysEndpoint(endpoint);

        CreateTemplateResponse response = client.getAcsResponse(request);
        result.append("* create configuration template: " + response.getTemplate().getTemplateName());
        result.append(LINE_BREAK);
    }

    /**
     * 判断模板是否已经存在
     *
     * @param templateName 模板名称
     * @return true 存在；false 不存在
     * @throws ClientException
     */
    private Boolean getTemplateExistStatus(String templateName, String endpoint) throws ClientException {
        ListTemplatesRequest listTemplatesRequest = new ListTemplatesRequest();
        listTemplatesRequest.setTemplateName(templateName);
        listTemplatesRequest.setSysEndpoint(endpoint);

        ListTemplatesResponse listTemplatesResponse = client.getAcsResponse(listTemplatesRequest);
        for (ListTemplatesResponse.Template template : listTemplatesResponse.getTemplates()) {
            if (template.getTemplateName().equals(templateName)) {
                return true;
            }
        }

        return false;
    }
}
