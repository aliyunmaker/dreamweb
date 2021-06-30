package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.common.EndpointConstants;
import cc.landingzone.dreamweb.model.AccountEcsInfo;
import cc.landingzone.dreamweb.sso.sp.SPHelper;
import cc.landingzone.dreamweb.thread.LogtailAutoConfigJob;
import cc.landingzone.dreamweb.utils.JsonUtils;
import cc.landingzone.dreamweb.utils.SlsUtils;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.*;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.request.ApplyConfigToMachineGroupRequest;
import com.aliyun.openservices.log.request.CreateConfigRequest;
import com.aliyun.openservices.log.request.CreateIndexRequest;
import com.aliyun.openservices.log.request.CreateMachineGroupRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesRequest;
import com.aliyuncs.ecs.model.v20140526.DescribeInstancesResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.sts.model.v20150401.GetCallerIdentityRequest;
import com.aliyuncs.sts.model.v20150401.GetCallerIdentityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class SlsAutoConfigService {

    @Autowired
    SystemConfigService systemConfigService;

    private static Logger logger = LoggerFactory.getLogger(SlsAutoConfigService.class);
    private static String LINE_BREAK = "<br>";
    private static String LINE_SEPARATED = "<hr>";

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        4,
        8,
        30,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue(10));

    /**
     * 获取EcsList
     *
     * @param accessKey AK
     * @param secretKey SK
     * @return 返回EcsList
     * @throws Exception
     */
    public List<AccountEcsInfo> getEcsList(String accessKey, String secretKey, String region, Boolean useVpc)
        throws Exception {
        DefaultProfile profile = DefaultProfile.getProfile(region, accessKey, secretKey);
        String resourceManagerEndpoint = EndpointConstants.getResourceManagerEndpoint(region, useVpc);

        List<Map<String, String>> accountList = SPHelper.listAccounts(profile, resourceManagerEndpoint);
        String endpoint = EndpointConstants.getEcsEndpoint(region, useVpc);
        DescribeInstancesRequest request = new DescribeInstancesRequest();
        request.setSysEndpoint(endpoint);

        List<AccountEcsInfo> accountEcsInfoList = new ArrayList<>();
        for (Map<String, String> account : accountList) {
            IAcsClient client = SPHelper.getSubAccountClinet(accessKey, secretKey, account.get("AccountId"), region);
            DescribeInstancesResponse response = client.getAcsResponse(request);

            AccountEcsInfo accountEcsInfo = new AccountEcsInfo();
            accountEcsInfo.setAccountId(account.get("AccountId"));
            accountEcsInfo.setDisplayName(account.get("DisplayName"));
            List<String> instanceIdList = new ArrayList<>();
            for (DescribeInstancesResponse.Instance instance : response.getInstances()) {
                instanceIdList.add(instance.getInstanceId());
            }
            accountEcsInfo.setInstanceIdList(instanceIdList);
            accountEcsInfoList.add(accountEcsInfo);
        }

        return accountEcsInfoList;
    }

    /**
     * 在ECS上安装并配置Logtail
     *
     * @param accountEcsInfoList 账号+ECS列表
     * @param accessKey          ak
     * @param secretKey          sk
     * @param action             install 安装；uninstall 还原
     * @throws Exception
     */
    public String initLogtail(List<AccountEcsInfo> accountEcsInfoList, String accessKey, String secretKey,
                              String action, String region, Boolean useVpc) throws Exception {
        StringBuilder result = new StringBuilder();

        DefaultProfile profile = DefaultProfile.getProfile(region, accessKey, secretKey);
        IAcsClient masterClient = new DefaultAcsClient(profile);

        // 获取主账号uid
        GetCallerIdentityRequest request = new GetCallerIdentityRequest();
        String stsEndpoint = EndpointConstants.getStsEndpoint(region, useVpc);
        request.setSysEndpoint(stsEndpoint);
        GetCallerIdentityResponse response = masterClient.getAcsResponse(request);
        String masterUid = response.getAccountId();

        // 打印参数
        result.append("inputs: ");
        result.append(LINE_BREAK);
        result.append("- masterAccount uid: " + masterUid);
        result.append(LINE_BREAK);
        result.append("- subAccount list: " + JsonUtils.toJsonString(accountEcsInfoList));
        result.append(LINE_BREAK);
        result.append(LINE_SEPARATED);

        result.append(
            "<big><b>1. " + SlsUtils.drawWithColor(action) + " logtail component on subAccount's Ecs</b></big>");
        result.append(LINE_BREAK);

        logger.info("开始执行 {} logtail", action);
        CountDownLatch countDownLatch = new CountDownLatch(accountEcsInfoList.size());
        List<Future<String>> futureList = new ArrayList<>(accountEcsInfoList.size());
        for (AccountEcsInfo accountEcsInfo : accountEcsInfoList) {
            IAcsClient client = SPHelper.getSubAccountClinet(accessKey, secretKey, accountEcsInfo.getAccountId(),
                region);
            Future<String> future = threadPoolExecutor.submit(
                new LogtailAutoConfigJob(accountEcsInfo.getAccountId(),
                    action,
                    client,
                    countDownLatch,
                    accountEcsInfo.getInstanceIdList(),
                    masterUid,
                    region,
                    useVpc));

            futureList.add(future);
        }
        countDownLatch.await();
        logger.info("所有线程执行完成");

        for (Future<String> future : futureList) {
            result.append(future.get());
            result.append(LINE_BREAK);
        }
        result.append(LINE_SEPARATED);

        return result.toString();
    }

    /**
     * 初始化Sls，创建Project和机器组
     *
     * @param accountEcsInfoList 账号+ecs
     * @param accessKey          ak
     * @param secretKey          sk
     * @param action             install 初始化； uninstall还原
     */
    public String initSlsService(List<AccountEcsInfo> accountEcsInfoList, String accessKey, String secretKey,
                                 String action, String region, Boolean useVpc) {
        StringBuilder result = new StringBuilder();

        String endpoint = EndpointConstants.getSlsEndpoint(region, useVpc);
        Client client = new Client(endpoint, accessKey, secretKey);

        result.append(
            "<big><b>2. " + SlsUtils.drawWithColor(action) + " sls configuration on master account</b></big>");
        result.append(LINE_BREAK);

        for (AccountEcsInfo accountEcsInfo : accountEcsInfoList) {
            // accountName作为Project名称，对account名称有限制
            // DisplayName：可以用汉字、英文字母、下划线和英文句点作为名称
            // ProjectName：只能用英文字母和数组，加上短划线
            String accountName = accountEcsInfo.getDisplayName().replace('_', '-');
            String accountId = accountEcsInfo.getAccountId();

            result.append("<b>start " + accountName + " sls config</b>");
            result.append(LINE_BREAK);
            result.append(LINE_SEPARATED);
            if ("install".equals(action)) {
                // 1. 创建Project和Logstore
                String projectAndLogstoreResult = createProjectAndLogstore(client, accountName);
                result.append(projectAndLogstoreResult);
                result.append(LINE_SEPARATED);

                // 2. 在Project下创建机器组
                String machineGroupResult = createMachineGroup(client, accountName, accountName, accountId);
                result.append(machineGroupResult);
                result.append(LINE_SEPARATED);

                // 3. 创建Logtail配置并应用到对应机器组
                String logtailConfigResult = createLogtailConfig(client, accountName, accountName, endpoint);
                result.append(logtailConfigResult);
                result.append(LINE_SEPARATED);
            } else {
                try {
                    client.DeleteProject(accountName);
                    result.append(SlsUtils.drawWithColor("delete") + " project: " + accountName);
                } catch (LogException e) {
                    result.append(e.getMessage());
                }
                result.append(LINE_SEPARATED);
            }
            result.append("<b>finish " + accountName + " sls config</b>");
            result.append(LINE_BREAK);
            result.append(LINE_SEPARATED);
        }

        return result.toString();
    }

    private String createProjectAndLogstore(Client client, String projectName) {
        StringBuilder result = new StringBuilder();

        // 1. 创建Project
        try {
            client.CreateProject(projectName, "auto create by dreamweb");
            result.append(SlsUtils.drawWithColor("create") + " project: " + projectName);
        } catch (LogException e) {
            result.append(e.getMessage());
        }
        result.append(LINE_SEPARATED);

        // 2. 创建Logstore
        String logstoreName = "nginx-access-log"; // 固定好的采集nginx日志
        int ttlInDay = 3; // 数据保存时间
        int shardCount = 1; // Shard数量
        LogStore logstore = new LogStore(logstoreName, ttlInDay, shardCount);

        try {
            client.CreateLogStore(projectName, logstore);
            result.append(SlsUtils.drawWithColor("create") + " logstore");
            result.append(LINE_BREAK);
            result.append("* output: " + logstore.ToJsonString());
        } catch (LogException e) {
            result.append(e.getMessage());
        }
        result.append(LINE_SEPARATED);

        // 3. 创建全文索引
        IndexLine indexLine = new IndexLine();
        List<String> token = new ArrayList<>();
        token.add("\n");
        token.add("\t");
        token.add("\r");
        indexLine.SetToken(token);
        Index index = new Index();
        index.SetLine(indexLine);
        try {
            CreateIndexRequest indexRequest = new CreateIndexRequest(projectName, logstoreName, index);
            client.CreateIndex(indexRequest);
            result.append(SlsUtils.drawWithColor("create") + " index");
            result.append(LINE_BREAK);
            result.append("* output: " + index.ToJsonString());
        } catch (LogException e) {
            result.append(e.getMessage());
        }

        return result.toString();
    }

    private String createMachineGroup(Client client, String projectName, String machineGroupName, String machineTag) {
        StringBuilder result = new StringBuilder();

        // 1. 机器组参数配置
        String machineIdentifyType = "userdefined";
        ArrayList<String> machineList = new ArrayList<>();
        machineList.add(machineTag);

        MachineGroup machineGroup = new MachineGroup();
        machineGroup.SetGroupName(machineGroupName);
        machineGroup.SetMachineIdentifyType(machineIdentifyType);
        machineGroup.SetMachineList(machineList);

        // 2. 创建机器组
        try {
            CreateMachineGroupRequest machineGroupRequest = new CreateMachineGroupRequest(projectName, machineGroup);
            client.CreateMachineGroup(machineGroupRequest);
            result.append(SlsUtils.drawWithColor("create") + " machine group");
            result.append(LINE_BREAK);
            result.append("* output: " + machineGroup.ToJsonString());
        } catch (LogException e) {
            result.append(e.getMessage());
        }
        result.append(LINE_BREAK);

        return result.toString();
    }

    /**
     * 创建LogtailConfig
     *
     * @param client
     * @param projectName
     * @param endpoint
     * @return
     */
    private String createLogtailConfig(Client client, String projectName, String groupName, String endpoint) {
        StringBuilder result = new StringBuilder();

        // 1. logtail config 所需参数
        String configName = "nginx-access-config";
        String logstoreName = "nginx-access-log"; // 固定好的采集nginx日志
        Config config = new Config();
        config.SetConfigName(configName);
        config.SetInputType("file");

        ConfigInputDetail configInputDetail = new ConfigInputDetail();
        configInputDetail.SetLogType("common_reg_log");
        configInputDetail.SetLogPath("/var/log/nginx/");
        configInputDetail.SetFilePattern("access.log");
        configInputDetail.SetTopicFormat("none");
        ArrayList<String> key = new ArrayList<>();
        key.add("content");
        configInputDetail.SetKey(key);
        configInputDetail.SetLogBeginRegex(".*");
        configInputDetail.SetRegex("(.*)");
        config.SetInputDetail(configInputDetail);

        config.SetOutputDetail(new ConfigOutputDetail(endpoint, logstoreName));

        // 2. 创建logtail Config
        try {
            CreateConfigRequest configRequest = new CreateConfigRequest(projectName, config);
            client.CreateConfig(configRequest);
            result.append(SlsUtils.drawWithColor("create") + " logtail config");
            result.append(LINE_BREAK);
            result.append("* output: " + config.ToJsonString());
        } catch (LogException e) {
            result.append(e.getMessage());
        }

        // 3. 将logtail config绑定到机器组上
        try {
            ApplyConfigToMachineGroupRequest applyConfigToMachineGroupRequest =
                new ApplyConfigToMachineGroupRequest(projectName, groupName, configName);
            client.ApplyConfigToMachineGroup(applyConfigToMachineGroupRequest);
            result.append("* attach config: " + configName + " to machineGroup: " + groupName);
        } catch (LogException e) {
            result.append(e.getMessage());
        }
        result.append(LINE_BREAK);

        return result.toString();
    }
}
