package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.TaskModel;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.*;
import com.aliyun.servicecatalog20210901.Client;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private PreViewService preViewService;

    @RequestMapping("/getMyTaskList.do")
    public void myTaskList (HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> list = taskService.createTaskQuery()//创建任务查询对象
                .taskAssignee(username)//指定个人任务查询
                .list();
        List<TaskModel> taskModelList = new ArrayList<>();
        if(list!=null && list.size()>0){
            for(Task task:list){
//                String processInfo = (String) taskService.getVariable(task.getId(), "processInfo");
                String starterName = (String) taskService.getVariable(task.getId(), "starterName");
                HistoricProcessInstance historicProcessInstance = historyService//与历史数据（历史表）相关的Service
                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询
                        .processInstanceId(task.getProcessInstanceId())//使用流程实例ID查询
                        .singleResult();

                TaskModel taskmodel = new TaskModel();
                taskmodel.setStartername(starterName);
                taskmodel.setProcesstime(DateUtil.dateTime2String(historicProcessInstance.getStartTime()));
                taskmodel.setTasktime(DateUtil.dateTime2String(task.getCreateTime()));
//                taskmodel.setProcessinfo(processInfo);
                taskmodel.setTaskid(task.getId());
                taskmodel.setTaskname(task.getName());
                taskmodel.setProcessid(task.getProcessInstanceId());
                taskModelList.add(taskmodel);
            }
        }
        result.setData(taskModelList);
        outputToJSON(response, result);

    }

    @RequestMapping("/complete.do")
    public void completeTaskById(HttpServletRequest request, HttpServletResponse response) {
         WebResult result = new WebResult();
        try {
            String taskids = request.getParameter("taskid");
            String processids = request.getParameter("processid");
            String delimeter = ",";
            String[] taskIds = taskids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);
            String[] processIds = processids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);

//            ExecutorService taskExecutor  = Executors.newCachedThreadPool();
//            final CountDownLatch latch = new CountDownLatch(taskIds.length);//用于判断所有的线程是否结束
//            System.out.println("个数=="+taskIds.length);

            for (int i = 0; i < taskIds.length; i++) {
                 String taskId = taskIds[i];
                 String processId = processIds[i];
//                 String processInfo = (String) taskService.getVariable(taskId, "processInfo");
//                 String starterName = (String) taskService.getVariable(taskId, "starterName");
                 Map<String, Object> variables = new HashMap<>();
//                 variables.put("processInfo", processInfo);
//                 variables.put("starterName", starterName);
                 variables.put("con", 1);
                 taskService.complete(taskId, variables);

                 ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
                 if (process == null) {
                     applyService.updateProcessState(processId, "已通过");
                     applyService.updateTask(processId, "无等待任务");

                     Integer flag = 0;
                     result.setData(flag);

// //                System.out.println("启动产品");
//                     Integer roleId = 1;
//                     String region = "cn-hangzhou";
//                     // 获取当前用户信息以及所需要使用的ram角色信息
//                     String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//                     User user = userService.getUserByLoginName(userName);
//                     UserRole userRole = userRoleService.getUserRoleById(roleId);
//
//                     productService.launchProduct(region, user, userRole);


                 } else {
                     String task = "等待" + taskService.createTaskQuery().processInstanceId(process.getId()).singleResult().getName();
                     applyService.updateTask(processId, task);

                     Integer flag = 1;
                     result.setData(flag);
                 }

//                String taskId = taskIds[i];
//                String processId = processIds[i];
//                Runnable run = new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String processInfo = (String) taskService.getVariable(taskId, "processInfo");
//                             String starterName = (String) taskService.getVariable(taskId, "starterName");
//                             Map<String, Object> variables = new HashMap<>();
//                             variables.put("processInfo", processInfo);
//                             variables.put("starterName", starterName);
//                             variables.put("con", 1);
//                             taskService.complete(taskId, variables);
//
//                             ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
//                             if (process == null) {
//                                 applyService.updateProcessState(processId, "已结束");
//                                 applyService.updateTask(processId, "无等待任务");
//
//             //                System.out.println("启动产品");
//                                 Integer roleId = 1;
//                                 String region = "cn-hangzhou";
//                                 // 获取当前用户信息以及所需要使用的ram角色信息
//                                 String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//                                 User user = userService.getUserByLoginName(userName);
//                                 UserRole userRole = userRoleService.getUserRoleById(roleId);
//
//                                 productService.launchProduct(region, user, userRole);
//
//
//                             } else {
//                                 String task = "等待" + taskService.createTaskQuery().processInstanceId(process.getId()).singleResult().getName();
//                                 applyService.updateTask(processId, task);
//                             }
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                };
//                taskExecutor.execute(run);
//
            }
//            try {
//                //等待所有线程执行完毕
//                latch.await();//主程序执行到await()函数会阻塞等待线程的执行，直到计数为0
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            taskExecutor.shutdown();//关闭线程池
//            //所有线程执行完毕,执行主线程
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        }
         outputToJSON(response, result);
    }

    @RequestMapping("/createProduct.do")
    public void createProduct(HttpServletRequest request, HttpServletResponse response) {
        try {
            String region = "cn-hangzhou";

            String processids = request.getParameter("processid");
            String delimeter = ",";
            String[] processIds = processids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);

            Map<String, Object> example = null;
            for (String processid : processIds) {
                example = getInfo(processid);
            }

            // 获取当前用户信息以及所需要使用的ram角色信息
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userService.getUserByLoginName(userName);
            UserRole userRole = userRoleService.getUserRoleById((Integer) example.get("角色ID"));

            //获取参数列表
            Map<String, String> inputs = new HashMap<>();
            inputs.put("zone_id", "cn-shanghai-l");
            inputs.put("vpc_cidr_block", "172.16.0.0/12");
            inputs.put("vswitch_cidr_block", "172.16.0.0/21");
            inputs.put("ecs_instance_type", "ecs.s6-c1m1.small");
            //产品版本ID
            //StackRegionId

            Client client = preViewService.createClient(region, user, userRole);
            String provisionedProductId = productService.launchProduct(client, inputs, example);
            System.out.println("ProvisionedProductId: " + provisionedProductId);


            productService.launchProduct(client, provisionedProductId, example);

//            productService.launchProduct(region, user, userRole);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


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

//        String processInfo = (String) taskService.getVariable(taskId, "processInfo");
//        String starterName = (String) taskService.getVariable(taskId, "starterName");
        Map<String, Object> variables=new HashMap<>();
//        variables.put("processInfo", processInfo);
//        variables.put("starterName", starterName);
        variables.put("con", 0);
        taskService.complete(taskId, variables);



        outputToJSON(response, result);
    }

    @RequestMapping("/getAllTaskList.do")
    public void getAllTaskList(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        // 获取“任务”查询器
        List<Task> tasks = taskService.createTaskQuery().list();
        List<TaskModel> taskModelList = new ArrayList<>();

        // 循环结果集
        tasks.forEach(task -> {
            TaskModel taskmodel = new TaskModel();
//            String processInfo = (String) taskService.getVariable(task.getId(), "processInfo");
            String starterName = (String) taskService.getVariable(task.getId(), "starterName");
            HistoricProcessInstance historicProcessInstance = historyService//与历史数据（历史表）相关的Service
                    .createHistoricProcessInstanceQuery()//创建历史流程实例查询
                    .processInstanceId(task.getProcessInstanceId())//使用流程实例ID查询
                    .singleResult();
            taskmodel.setStartername(starterName);
            taskmodel.setProcesstime(DateUtil.dateTime2String(historicProcessInstance.getStartTime()));
            taskmodel.setTasktime(DateUtil.dateTime2String(task.getCreateTime()));
//            taskmodel.setProcessinfo(processInfo);
            taskmodel.setTaskid(task.getId());
            taskmodel.setTaskname(task.getName());
            taskmodel.setProcessid(task.getProcessInstanceId());
            taskmodel.setAssignee(task.getAssignee());
//                taskmodel.setAssignee(task.getAssignee());
            taskModelList.add(taskmodel);
        });
        result.setData(taskModelList);
        outputToJSON(response, result);
    }

    @RequestMapping("/getInfo.do")
    public void getInfo(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String processid = request.getParameter("processid");
        Map<String, Object> example = getInfo(processid);
        result.setData(example);
        outputToJSON(response, result);
    }

    public Map<String, Object> getInfo(String processid) {
        Task task = taskService.createTaskQuery().processInstanceId(processid).singleResult();
        String application = null;
        String scene = null;
        String productId = null;
        String exampleName = null;
        String processInfo = null;
        String starterName = null;
        Integer roleId = null;
        if (task != null) {
            application = (String) taskService.getVariable(task.getId(), "application");
            scene = (String) taskService.getVariable(task.getId(), "scene");
            productId = (String) taskService.getVariable(task.getId(), "productId");
            exampleName = (String) taskService.getVariable(task.getId(), "exampleName");
            processInfo = (String) taskService.getVariable(task.getId(), "processInfo");
            starterName = (String) taskService.getVariable(task.getId(), "starterName");
            roleId = (Integer) taskService.getVariable(task.getId(), "roleId");
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
                if(historicVariableInstance.getVariableName().equals("processInfo")) {
                    if(historicVariableInstance.getValue()!=null){
                        processInfo = (String) historicVariableInstance.getValue();
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
        example.put("参数信息", processInfo);
        example.put("申请人", starterName);
        example.put("角色ID", roleId);

        return example;
    }


}

