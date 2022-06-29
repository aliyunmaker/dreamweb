package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.Apply;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.service.*;
import com.aliyun.servicecatalog20210901.Client;
import com.aliyun.servicecatalog20210901.models.GetProvisionedProductPlanRequest;
import com.aliyun.servicecatalog20210901.models.GetProvisionedProductPlanResponse;
import com.aliyun.servicecatalog20210901.models.GetProvisionedProductPlanResponseBody;
import org.activiti.engine.*;
import cc.landingzone.dreamweb.model.WebResult;
import org.activiti.engine.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工作流相关控制操作
 *
 * @author: laodou
 * @createDate: 2022/6/21
 */
@Controller
@RequestMapping("/apply")
public class ProcessController extends BaseController {

    @Autowired
    private ApplyService applyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ServiceCatalogViewService serviceCatalogViewService;

    /**
     * 流程部署
     *
     * @throws Exception
     */
    public void processDeployment() {
        // 创建流程部署工具
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        // bpm 文件路径
        String bpmnResourcePath = "processes/example/apply/ServiceCatalog.bpmn";
//        // png 文件路径
//        String pngResourcePath = "processes/example/apply/ApplyProcess.png";
        // 流程名
        String processName = "申请流程";

        /*
         * 流程部署，其中就需要文件对应的路径
         * 同时我们再部署的时候可以给这个流程起一个名字
         * 部署后会返回一个部署对象
         */
        Deployment deployment = deploymentBuilder.name(processName).addClasspathResource(bpmnResourcePath).deploy();
    }

    /**
     * 查询部署的流程最新版本
     *
     * @throws Exception
     * @param: 流程定义ID
     */
    @GetMapping("/processDefinitionQueryFinal.do")
    public void processDefinitionQueryFinal(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        // 获取"流程定义"查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        // 这里没有设置查询条件，就是查询目前所有的流程，每个流程的最新版本
        List<ProcessDefinition> processDefinitions = processDefinitionQuery.latestVersion().list();

        if (processDefinitions.isEmpty()) {
            processDeployment();
            processDefinitions = processDefinitionQuery.latestVersion().list();
        }
        // 循环结果集
        processDefinitions.forEach(definition -> {
            result.setData(definition.getId());
        });
        outputToJSON(response, result);

    }

    /**
     * 申请信息存工作流表，开启预检
     *
     * @return 流程实例ID
     * @throws Exception
     * @param: 流程定义ID
     */
    @GetMapping("/startPlan.do")
    public void startPlan(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String processDefinitionId = request.getParameter("definitionId");
            String application = request.getParameter("select_Application");
            String scene = request.getParameter("select_Scene");
            String productId = request.getParameter("productId");
            Integer roleId = Integer.valueOf(request.getParameter("roleId"));

            String PlanId = request.getParameter("PlanId");
            JSONObject planIdJson = JSON.parseObject(PlanId);
            String planId = planIdJson.getString("PlanId");
            GetProvisionedProductPlanRequest request1 = new GetProvisionedProductPlanRequest();
            request1.setPlanId(planId);
            String region = "cn-hangzhou";
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(userName);
            UserRole userRole = userRoleService.getUserRoleById(roleId);
            Client client = serviceCatalogViewService.createClient(region, user, userRole, productId);
            GetProvisionedProductPlanResponse response1 = client.getProvisionedProductPlan(request1);
            JSONObject parameter = new JSONObject();
            for (GetProvisionedProductPlanResponseBody.GetProvisionedProductPlanResponseBodyPlanDetailParameters para :
                    response1.getBody().getPlanDetail().parameters) {
                parameter.put(para.getParameterKey(), para.getParameterValue());
            }

            Apply apply = new Apply();
            apply.setStarterName(userName);
            apply.setRoleId(roleId);
            apply.setApplication(application);
            apply.setScene(scene);

            String time = response1.getBody().getPlanDetail().createTime;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //设置时区UTC
            df.setTimeZone(TimeZone.getTimeZone("UTC")); //格式化，转当地时区时间
            Date timeData = df.parse(time);
            df.applyPattern("yyyy-MM-dd HH:mm:ss"); //默认时区
            df.setTimeZone(TimeZone.getDefault());
            apply.setProcessTime(df.format(timeData));

            apply.setProcessState("预检中");
            apply.setParameters(JSON.toJSONString(parameter));
            apply.setProductId(productId);
            apply.setRegion(response1.getBody().getPlanDetail().stackRegionId);
            apply.setVersionId(response1.getBody().getPlanDetail().productVersionId);
            apply.setProcessDefinitionId(processDefinitionId);
            apply.setCond("未拒绝");
            apply.setExampleName(response1.getBody().getPlanDetail().provisionedProductName);
            apply.setPlanId(planId);
            applyService.saveApply(apply);

            result.setData(planId);
            outputToJSON(response, result);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 获取我的工作流申请列表
     *
     * @return 工作流列表
     * @throws Exception
     * @param: 当前登录用户名
     */
    @GetMapping("/getMyAsk.do")
    public void getMyAsk(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Apply> list = applyService.listApply(userName);
        result.setData(list);
        outputToJSON(response, result);
    }

}