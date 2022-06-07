package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.TaskModel;
import cc.landingzone.dreamweb.model.WebResult;
import cc.landingzone.dreamweb.service.ApplyService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
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

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                String processInfo = (String) taskService.getVariable(task.getId(), "processInfo");
                String starterName = (String) taskService.getVariable(task.getId(), "starterName");
                HistoricProcessInstance historicProcessInstance = historyService//与历史数据（历史表）相关的Service
                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询
                        .processInstanceId(task.getProcessInstanceId())//使用流程实例ID查询
                        .singleResult();

                TaskModel taskmodel = new TaskModel();
                taskmodel.setStartername(starterName);
                taskmodel.setProcesstime(DateUtil.dateTime2String(historicProcessInstance.getStartTime()));
                taskmodel.setTasktime(DateUtil.dateTime2String(task.getCreateTime()));
                taskmodel.setProcessinfo(processInfo);
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
        String taskids = request.getParameter("taskid");
        String processids = request.getParameter("processid");
        String delimeter = ",";
        String[] taskIds = taskids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);
        String[] processIds = processids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);
        for(int i = 0; i < taskIds.length ; i++){
            String taskId = taskIds[i];
            String processId = processIds[i];
            String processInfo = (String) taskService.getVariable(taskId, "processInfo");
            String starterName = (String) taskService.getVariable(taskId, "starterName");
            Map<String, Object> variables=new HashMap<>();
            variables.put("processInfo", processInfo);
            variables.put("starterName", starterName);
            variables.put("con", 1);
            taskService.complete(taskId, variables);

            ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
            if (process == null) {
                applyService.updateProcessState(processId, "已结束");
                applyService.updateTask(processId, "无等待任务");
            } else {
                String task = "等待" + taskService.createTaskQuery().processInstanceId(process.getId()).singleResult().getName();
                applyService.updateTask(processId, task);
            }
        }
        outputToJSON(response, result);
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
        applyService.updateProcessState(processId, "已结束");
        applyService.updateTask(processId, "无等待任务");

        String processInfo = (String) taskService.getVariable(taskId, "processInfo");
        String starterName = (String) taskService.getVariable(taskId, "starterName");
        Map<String, Object> variables=new HashMap<>();
        variables.put("processInfo", processInfo);
        variables.put("starterName", starterName);
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
            String processInfo = (String) taskService.getVariable(task.getId(), "processInfo");
            String starterName = (String) taskService.getVariable(task.getId(), "starterName");
            HistoricProcessInstance historicProcessInstance = historyService//与历史数据（历史表）相关的Service
                    .createHistoricProcessInstanceQuery()//创建历史流程实例查询
                    .processInstanceId(task.getProcessInstanceId())//使用流程实例ID查询
                    .singleResult();
            taskmodel.setStartername(starterName);
            taskmodel.setProcesstime(DateUtil.dateTime2String(historicProcessInstance.getStartTime()));
            taskmodel.setTasktime(DateUtil.dateTime2String(task.getCreateTime()));
            taskmodel.setProcessinfo(processInfo);
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

}
