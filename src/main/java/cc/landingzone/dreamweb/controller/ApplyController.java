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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/apply")
public class ApplyController extends BaseController{

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

    public void applyDelopment() {
        // 创建流程部署工具
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();

        // bpm 文件路径
        String bpmnResourcePath = "processes/example/apply/ApplyProcess.bpmn";
        // png 文件路径
        String pngResourcePath = "processes/example/apply/ApplyProcess.png";
        // 流程名
        String processName = "申请流程";

        /*
         * 流程部署，其中就需要两个文件对应的路径
         * 同时我们再部署的时候可以给这个流程起一个名字
         * 部署后会返回一个部署对象
         */
        Deployment deployment = deploymentBuilder.name(processName).addClasspathResource(bpmnResourcePath)
                .addClasspathResource(pngResourcePath).deploy();

        System.out.println("流程部署成功，流程部署ID：" + deployment.getId());

    }

    @GetMapping("/processDefinitionQueryFinal.do")
    public void processDefinitionQueryFinal(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        // 获取"流程定义"查询器
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();

        // 这里没有设置查询条件，就是查询目前所有的流程，每个流程的最新版本
        List<ProcessDefinition> processDefinitions = processDefinitionQuery.latestVersion().list();

        if(processDefinitions.isEmpty()) {
            applyDelopment();
            processDefinitions = processDefinitionQuery.latestVersion().list();
        }
        // 循环结果集
        processDefinitions.forEach(definition -> {
            result.setData(definition.getId());
        });
        outputToJSON(response, result);

    }

    @GetMapping("/startProcessByDefinitionId.do")
    public void startProcessByDefinitionId(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        // 流程定义 ID
        String processDefinitionId = request.getParameter("definitionId");
        String application = request.getParameter("select_Application");
        String scene = request.getParameter("select_Scene");
        String productId = request.getParameter("productId");
        String exampleName = request.getParameter("exampleName");
        Integer roleId = Integer.valueOf(request.getParameter("roleId"));

        // 启动流程
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        identityService.setAuthenticatedUserId(username);

        String info="asdbkjsankasf";//用户选择的参数列表
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("processInfo", info);//userKey在上文的流程变量中指定了
        variables.put("starterName", username);
        variables.put("application", application);
        variables.put("scene", scene);
        variables.put("productId", productId);
        variables.put("exampleName", exampleName);
        variables.put("roleId", roleId);

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);

        String task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult().getName();
        Apply apply = new Apply();
        apply.setStartername(username);
        apply.setProcesstime(DateUtil.dateTime2String(processInstance.getStartTime()));
        apply.setProcessid(processInstance.getProcessInstanceId());
        apply.setProcessstate("审批中");
        apply.setProcessinfo(info);
        apply.setProcessdefinitionid(processDefinitionId);
        apply.setCond("未拒绝");
        apply.setTask("等待" + task);
        applyService.addApply(apply);

        result.setData(processInstance.getProcessInstanceId());
        outputToJSON(response, result);
    }

    @GetMapping("/getMyAsk.do")
    public void getMyAsk (HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println(username);
        List<Apply> list = applyService.getApply(username);
//        for (Apply apply : list) {
//            ProcessInstance process = runtimeService.createProcessInstanceQuery().processDefinitionId(apply.getProcessdefinitionid()).processInstanceId(apply.getProcessid()).singleResult();
//            if (process == null) {
//                apply.setProcessstate("已结束");
//                applyService.updateProcessState(apply.getProcessid(), "已结束");
//                apply.setTask("无");
//                applyService.updateTask(apply.getProcessid(), "无等待任务");
//            } else {
//                apply.setProcessstate("运行中");
//                apply.setTask("等待" + taskService.createTaskQuery().processInstanceId(process.getId()).singleResult().getName());
//                applyService.updateTask(apply.getProcessid(), apply.getTask());
//            }
//        }
        result.setData(list);
        outputToJSON(response, result);
    }

}