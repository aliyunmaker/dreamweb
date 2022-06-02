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

        // 这里我们没有设置查询条件，就是查询目前所有的流程，每个流程的最新版本
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
//        System.out.println(processDefinitionId);
        // 启动流程
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        identityService.setAuthenticatedUserId(username);

//        productService.updateExample();

        String info="asdbkjsankasf";//脑补一下这个是从前台传过来的数据
        
        String text1 = "text1";
        String text2 = "text2";
        String text3 = "text3";
        String text4 = "text4";
        Map<String, String> info2 = new HashMap<String,String>();
        info2.put("text1", text1);
        info2.put("text2", text2);
        info2.put("text3", text3);
        info2.put("text4", text4);

        Map<String, Object> variables = new HashMap<>();
        variables.put("processInfo", info);//userKey在上文的流程变量中指定了
        variables.put("starterName", username);

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId, variables);

        Apply apply = new Apply();
        apply.setStartername(username);
        apply.setProcesstime(DateUtil.dateTime2String(processInstance.getStartTime()));
        apply.setProcessid(processInstance.getProcessInstanceId());
        apply.setProcessstate("运行中");
        apply.setProcessinfo(info);
        apply.setProcessdefinitionid(processDefinitionId);
        apply.setCond("未拒绝");
        applyService.addApply(apply);

        // System.out.println("流程启动成功，流程实例ID：" + processInstance.getProcessInstanceId());
        result.setData(processInstance.getProcessInstanceId());
        outputToJSON(response, result);
    }

    @GetMapping("/getMyAsk.do")
    public void getMyAsk (HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        System.out.println(username);
        List<Apply> list = applyService.getApply(username);
        for (Apply apply : list) {
            ProcessInstance process = runtimeService.createProcessInstanceQuery().processDefinitionId(apply.getProcessdefinitionid()).processInstanceId(apply.getProcessid()).singleResult();
            if (process == null) {
                apply.setProcessstate("已结束");
                applyService.updateProcessState(apply.getProcessid(), "已结束");
                apply.setTask("无");
                applyService.updateTask(apply.getProcessid(), "无等待任务");
            } else {
                apply.setProcessstate("运行中");
                apply.setTask("等待" + taskService.createTaskQuery().processInstanceId(process.getId()).singleResult().getName());
                applyService.updateTask(apply.getProcessid(), apply.getTask());
            }
        }
        result.setData(list);
        outputToJSON(response, result);
    }

}