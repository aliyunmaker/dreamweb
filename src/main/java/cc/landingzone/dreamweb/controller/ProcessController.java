package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.Apply;
import cc.landingzone.dreamweb.service.ApplyService;
import cc.landingzone.dreamweb.service.ProductService;
import org.activiti.engine.*;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.utils.DateUtil;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 工作流相关控制操作
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Controller
@RequestMapping("/apply")
public class ProcessController extends BaseController{

    @Autowired
    private ApplyService applyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ProductService productService;

    /**
         * 流程部署
         *
         *
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
    * 
    * @param: 流程定义ID
    * @throws Exception
    */
    @GetMapping("/processDefinitionQueryFinal.do")
    public void processDefinitionQueryFinal(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        // 获取"流程定义"查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        // 这里没有设置查询条件，就是查询目前所有的流程，每个流程的最新版本
        List<ProcessDefinition> processDefinitions = processDefinitionQuery.latestVersion().list();

        if(processDefinitions.isEmpty()) {
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
         * 启动流程
         *
         * @param: 流程定义ID
         * @return 流程实例ID
         * @throws Exception
         */
    @GetMapping("/startProcessByDefinitionId.do")
    public void startProcessByDefinitionId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String processDefinitionId = request.getParameter("definitionId");
        String application = request.getParameter("select_Application");
        String scene = request.getParameter("select_Scene");
        String productId = request.getParameter("productId");
        String exampleName = request.getParameter("exampleName");
        Integer roleId = Integer.valueOf(request.getParameter("roleId"));
        String parameter = request.getParameter("parameter");
        JSONObject parameters = JSON.parseObject(parameter);
        String region = parameters.getString("stackRegionId");
        String versionId = parameters.getString("prodocutVersionId");
        parameters.remove("stackRegionId");
        parameters.remove("productVersionId");

        // 启动流程
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        identityService.setAuthenticatedUserId(userName);
        String info=parameters.getString("parameters");//用户选择的参数列表
        Map<String, Object> variables = new HashMap<>();
        variables.put("parameters", info);//userKey在上文的流程变量中指定了
        variables.put("starterName", userName);
        variables.put("application", application);
        variables.put("scene", scene);
        variables.put("productId", productId);
        variables.put("exampleName", exampleName);
        variables.put("roleId", roleId);
        variables.put("region", region);
        variables.put("versionId", versionId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);
        String task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult().getName();
        Apply apply = new Apply();
        apply.setStarterName(userName);
        apply.setProcessTime(DateUtil.dateTime2String(processInstance.getStartTime()));
        apply.setProcessId(processInstance.getProcessInstanceId());
        apply.setProcessState("审批中");
        apply.setParameters(info);
        apply.setRegion(region);
        apply.setVersionId(versionId);
        apply.setProcessDefinitionId(processDefinitionId);
        apply.setCond("未拒绝");
        apply.setTask("等待" + task);
        applyService.saveApply(apply);

        result.setData(processInstance.getProcessInstanceId());
        outputToJSON(response, result);
    }

         /**
         * 获取我的工作流申请列表
         *
         * @param: 当前登录用户名
         * @return 工作流列表
         * @throws Exception
         */
    @GetMapping("/getMyAsk.do")
    public void getMyAsk (HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Apply> list = applyService.listApply(userName);
        result.setData(list);
        outputToJSON(response, result);
    }

}