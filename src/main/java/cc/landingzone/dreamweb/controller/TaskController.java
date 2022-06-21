package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.Assignment;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.*;
import com.alibaba.fastjson.JSON;
import com.aliyun.servicecatalog20210901.Client;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import cc.landingzone.dreamweb.utils.DateUtil;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 对工作流任务进行操作
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Controller
@RequestMapping("/task")
public class TaskController extends BaseController{

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private HistoryService historyService;

    @Autowired
    private ApplyService applyService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ServiceCatalogViewService serviceCatalogViewService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private ProvisionedProductService provisionedProductService;

    /**
         * 获取登录用户待办任务列表
         *
         * @param: 当前登录人
         * @return 任务列表
         * @throws Exception
         */
    @RequestMapping("/getMyTaskList.do")
    public void myTaskList (HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> list = taskService.createTaskQuery()//创建任务查询对象
                .taskAssignee(username)//指定个人任务查询
                .list();
        List<Assignment> assignmentList = new ArrayList<>();
        if(list!=null && list.size()>0){
            for(Task task:list){
                String starterName = (String) taskService.getVariable(task.getId(), "starterName");
                HistoricProcessInstance historicProcessInstance = historyService//与历史数据（历史表）相关的Service
                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询
                        .processInstanceId(task.getProcessInstanceId())//使用流程实例ID查询
                        .singleResult();

                Assignment taskmodel = new Assignment();
                taskmodel.setStartername(starterName);
                taskmodel.setProcesstime(DateUtil.dateTime2String(historicProcessInstance.getStartTime()));
                taskmodel.setTasktime(DateUtil.dateTime2String(task.getCreateTime()));
                taskmodel.setTaskid(task.getId());
                taskmodel.setTaskname(task.getName());
                taskmodel.setProcessid(task.getProcessInstanceId());
                assignmentList.add(taskmodel);
            }
        }
        result.setData(assignmentList);
        outputToJSON(response, result);

    }

    /**
         * 完成任务
         *
         * @param: 任务ID、流程实例ID
         * 
         * @throws Exception
         */
    @RequestMapping("/complete.do")
    public void completeTaskById(HttpServletRequest request, HttpServletResponse response) {
         WebResult result = new WebResult();
        try {
            String taskids = request.getParameter("taskid");
            String processids = request.getParameter("processid");
            String delimeter = ",";
            String[] taskIds = taskids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);
            String[] processIds = processids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);

            for (int i = 0; i < taskIds.length; i++) {
                 String taskId = taskIds[i];
                 String processId = processIds[i];
                 Map<String, Object> variables = new HashMap<>();
                 variables.put("con", 1);
                 taskService.complete(taskId, variables);

                 ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
                 if (process == null) {
                     applyService.updateProcessState(processId, "已通过");
                     applyService.updateTask(processId, "无等待任务");
                     createProduct(processId);  //审批通过后启动产品

                     Integer flag = 0;
                     result.setData(flag);

                 } else {
                     String task = "等待" + taskService.createTaskQuery().processInstanceId(process.getId()).singleResult().getName();
                     applyService.updateTask(processId, task);

                     Integer flag = 1;
                     result.setData(flag);
                 }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
         outputToJSON(response, result);
    }

    /**
         * 启动产品
         *
         * @param: 工作流流程ID
         *
         * @throws Exception
         */
    public void createProduct(String processid) {
        try {
            String region = "cn-hangzhou";
            Map<String, Object> example = getInfo(processid);

            // 获取申请人信息以及所使用的ram角色信息
            String userName = (String) example.get("申请人");
            User user = userService.getUserByLoginName(userName);
            UserRole userRole = userRoleService.getUserRoleById((Integer) example.get("角色ID"));

            String parameters = (String) example.get("参数信息");
            Map<String, String> inputs = (Map<String, String>) JSON.parse(parameters);

            Client client = serviceCatalogViewService.createClient(region, user, userRole);
            String provisionedProductId = provisionedProductService.launchProduct(client, inputs, example);

            provisionedProductService.addProvisionedProduct(client, provisionedProductId, example);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
         * 拒绝任务
         *
         * @param: 任务ID、流程ID、审批意见
         * 
         * @throws Exception
         */
    @RequestMapping("/reject.do")
    public void rejectTaskById(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();

        String opinion = request.getParameter("opinion");
        String taskId = request.getParameter("taskid");
        String processId = request.getParameter("processid");

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        applyService.updateCond(task.getProcessInstanceId(), "拒绝");
        applyService.updateOpinion(task.getProcessInstanceId(), opinion);
        applyService.updateProcessState(processId, "已拒绝");
        applyService.updateTask(processId, "无等待任务");

        Map<String, Object> variables=new HashMap<>();
        variables.put("con", 0);
        taskService.complete(taskId, variables);



        outputToJSON(response, result);
    }

    /**
         * 获取当前运行的所有工作流任务列表
         *
         * 
         * @return 任务列表
         * @throws Exception
         */
    @RequestMapping("/getAllTaskList.do")
    public void getAllTaskList(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        // 获取“任务”查询器
        List<Task> tasks = taskService.createTaskQuery().list();
        List<Assignment> assignmentList = new ArrayList<>();

        // 循环结果集
        tasks.forEach(task -> {
            Assignment taskmodel = new Assignment();
            String starterName = (String) taskService.getVariable(task.getId(), "starterName");
            HistoricProcessInstance historicProcessInstance = historyService//与历史数据（历史表）相关的Service
                    .createHistoricProcessInstanceQuery()//创建历史流程实例查询
                    .processInstanceId(task.getProcessInstanceId())//使用流程实例ID查询
                    .singleResult();
            taskmodel.setStartername(starterName);
            taskmodel.setProcesstime(DateUtil.dateTime2String(historicProcessInstance.getStartTime()));
            taskmodel.setTasktime(DateUtil.dateTime2String(task.getCreateTime()));
            taskmodel.setTaskid(task.getId());
            taskmodel.setTaskname(task.getName());
            taskmodel.setProcessid(task.getProcessInstanceId());
            taskmodel.setAssignee(task.getAssignee());
            assignmentList.add(taskmodel);
        });
        result.setData(assignmentList);
        outputToJSON(response, result);
    }

    /**
         * 获取任务详情
         *
         * @param: 流程ID
         * @return 流程实例信息
         * @throws Exception
         */
    @RequestMapping("/getInfo.do")
    public void getInfo(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String processid = request.getParameter("processid");
        Map<String, Object> example = getInfo(processid);
        result.setData(example);
        outputToJSON(response, result);
    }

    /**
         * 获取任务详情
         *
         * @param: 流程ID
         * @return 流程实例信息
         * @throws Exception
         */
    public Map<String, Object> getInfo(String processid) {
        Task task = taskService.createTaskQuery().processInstanceId(processid).singleResult();
        String application = null;
        String scene = null;
        String productId = null;
        String exampleName = null;
        String parameters = null;
        String starterName = null;
        Integer roleId = null;
        String region = null;
        String versionid = null;
        if (task != null) {
            application = (String) taskService.getVariable(task.getId(), "application");
            scene = (String) taskService.getVariable(task.getId(), "scene");
            productId = (String) taskService.getVariable(task.getId(), "productId");
            exampleName = (String) taskService.getVariable(task.getId(), "exampleName");
            parameters = (String) taskService.getVariable(task.getId(), "parameters");
            starterName = (String) taskService.getVariable(task.getId(), "starterName");
            roleId = (Integer) taskService.getVariable(task.getId(), "roleId");
            region = (String) taskService.getVariable(task.getId(), "region");
            versionid = (String) taskService.getVariable(task.getId(), "versionid");

        } else {
            List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(processid).list();
            for(HistoricVariableInstance historicVariableInstance : list) {
                if(historicVariableInstance.getVariableName().equals("application")) {
                    if(historicVariableInstance.getValue()!=null){
                        application = (String) historicVariableInstance.getValue();
                    }
                }
                if(historicVariableInstance.getVariableName().equals("scene")) {
                    if(historicVariableInstance.getValue()!=null){
                        scene = (String) historicVariableInstance.getValue();
                    }
                }
                if(historicVariableInstance.getVariableName().equals("productId")) {
                    if(historicVariableInstance.getValue()!=null){
                        productId = (String) historicVariableInstance.getValue();
                    }
                }
                if(historicVariableInstance.getVariableName().equals("exampleName")) {
                    if(historicVariableInstance.getValue()!=null){
                        exampleName = (String) historicVariableInstance.getValue();
                    }
                }
                if(historicVariableInstance.getVariableName().equals("parameters")) {
                    if(historicVariableInstance.getValue()!=null){
                        parameters = (String) historicVariableInstance.getValue();
                    }
                }
                if(historicVariableInstance.getVariableName().equals("region")) {
                    if(historicVariableInstance.getValue()!=null){
                        region = (String) historicVariableInstance.getValue();
                    }
                }
                if(historicVariableInstance.getVariableName().equals("versionid")) {
                    if(historicVariableInstance.getValue()!=null){
                        versionid = (String) historicVariableInstance.getValue();
                    }
                }
                if(historicVariableInstance.getVariableName().equals("starterName")) {
                    if(historicVariableInstance.getValue()!=null){
                        starterName = (String) historicVariableInstance.getValue();
                    }
                }
                if(historicVariableInstance.getVariableName().equals("roleId")) {
                    if(historicVariableInstance.getValue()!=null){
                        roleId = (Integer) historicVariableInstance.getValue();
                    }
                }
            }
        }
        Map<String, Object> example = new HashMap<>();
        example.put("应用", application);
        example.put("场景", scene);
        example.put("产品ID", productId);
        example.put("实例名称", exampleName);
        example.put("参数信息", parameters);
        example.put("地域", region);
        example.put("版本ID", versionid);
        example.put("申请人", starterName);
        example.put("角色ID", roleId);

        return example;
    }


}

