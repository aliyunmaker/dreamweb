package cc.landingzone.dreamweb.controller;

import cc.landingzone.dreamweb.model.*;
import cc.landingzone.dreamweb.service.*;
import com.aliyun.servicecatalog20210901.Client;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import cc.landingzone.dreamweb.utils.DateUtil;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 对工作流任务进行操作
 *
 * @author: laodou
 * @createDate: 2022/6/21
 */
@Controller
@RequestMapping("/task")
public class TaskController extends BaseController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private ServiceCatalogViewService serviceCatalogViewService;

    @Autowired
    private ProvisionedProductService provisionedProductService;

    @Autowired
    private ProductVersionService productVersionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserProductService userProductService;


    /**
     * 获取登录用户待办任务列表
     *
     * @return 任务列表
     * @throws Exception
     * @param: 当前登录人
     */
    @RequestMapping("/getMyTaskList.do")
    public void getMyTaskList(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> list = taskService.createTaskQuery()//创建任务查询对象
                .taskAssignee(username)//指定个人任务查询
                .list();
        List<Assignment> assignmentList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (Task task : list) {
                Integer applicationId = (Integer) taskService.getVariable(task.getId(), "applicationId");
                System.out.println(applicationId);
                Application application = applicationService.getApplicationById(applicationId);
                if (application == null) {
                    continue;
                }
                String starterName = userService.getUserById(application.getStarterId()).getLoginName();
                String servicecatalogPlanId = application.getServicecatalogPlanId();

                HistoricProcessInstance historicProcessInstance = historyService//与历史数据（历史表）相关的Service
                        .createHistoricProcessInstanceQuery()//创建历史流程实例查询
                        .processInstanceId(task.getProcessInstanceId())//使用流程实例ID查询
                        .singleResult();

                Assignment assignment = new Assignment();
                assignment.setServicecatalogPlanId(servicecatalogPlanId);
                assignment.setStarterName(starterName);
                assignment.setProcessTime(DateUtil.dateTime2String(historicProcessInstance.getStartTime()));
                assignment.setTaskTime(DateUtil.dateTime2String(task.getCreateTime()));
                assignment.setTaskId(task.getId());
                assignment.setTaskName(task.getName());
                assignment.setProcessId(task.getProcessInstanceId());
                assignmentList.add(assignment);
            }
        }
        Collections.sort(assignmentList);
        if(assignmentList.size() > (start + limit)) {
            result.setData(assignmentList.subList(start, start + limit));
        } else if(assignmentList.size() > start){
            result.setData(assignmentList.subList(start, assignmentList.size()));
        }
        result.setTotal(assignmentList.size());
        System.out.println(assignmentList.size());
        outputToJSON(response, result);

    }

    /**
     * 完成任务
     *
     * @throws Exception
     * @param: 任务ID、流程实例ID
     */
    @RequestMapping("/complete.do")
    public void completeTaskById(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        try {
            String taskids = request.getParameter("taskId");
            String processids = request.getParameter("processId");
            String planids = request.getParameter("planId");
            String delimeter = ",";
            String[] taskIds = taskids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);
            String[] processIds = processids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);
            String[] planIds = planids.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").split(delimeter);

            for (int i = 0; i < taskIds.length; i++) {
                String taskId = taskIds[i];
                String processId = processIds[i];
                String planId = planIds[i];
                Map<String, Object> variables = new HashMap<>();
                variables.put("con", 1);
                taskService.complete(taskId, variables);

                ProcessInstance process = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
                if (process == null) {
                    applicationService.updateProcessStateByProcessId(processId, "已通过");
                    applicationService.updateTaskByProcessId(processId, "无等待任务");
                    createProduct(planId);  //审批通过后启动产品
                    Integer flag = 0;
                    result.setData(flag);
                } else {
                    String task = "等待" + taskService.createTaskQuery().processInstanceId(process.getId()).singleResult().getName();
                    applicationService.updateTaskByProcessId(processId, task);

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
     * @throws Exception
     * @param: 工作流流程ID
     */
    public void createProduct(String planId) {
        try {
            String region = "cn-hangzhou";
            Map<String, Object> example = getInfo(planId);

            // 获取申请人信息以及所使用的ram角色信息
            String userName = (String) example.get("申请人");
            User user = userService.getUserByLoginName(userName);
            UserRole userRole = userRoleService.getUserRoleById((Integer) example.get("角色ID"));

            Client client = serviceCatalogViewService.createClient(region, user, userRole,(String) example.get("产品ID"));// 创建终端
            provisionedProductService.executePlan(client, planId);// 启动产品并返回实例ID
            provisionedProductService.saveProvisionedProduct(client, planId, example);// 查询实例信息并存入数据库
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 拒绝任务
     *
     * @throws Exception
     * @param: 任务ID、流程ID、审批意见
     */
    @RequestMapping("/reject.do")
    public void rejectTaskById(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();

        String opinion = request.getParameter("opinion");
        String taskId = request.getParameter("taskId");
        String processId = request.getParameter("processId");

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        applicationService.updateCondByProcessId(task.getProcessInstanceId(), "拒绝");
        applicationService.updateOpinionByProcessId(task.getProcessInstanceId(), opinion);
        applicationService.updateProcessStateByProcessId(processId, "已拒绝");
        applicationService.updateTaskByProcessId(processId, "无等待任务");

        Map<String, Object> variables = new HashMap<>();
        variables.put("con", 0);
        taskService.complete(taskId, variables);

        outputToJSON(response, result);
    }

    /**
     * 获取当前运行的所有工作流任务列表
     *
     * @return 任务列表
     * @throws Exception
     */
    @RequestMapping("/getAllTaskList.do")
    public void getAllTaskList(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        Integer start = Integer.valueOf(request.getParameter("start"));
        Integer limit = Integer.valueOf(request.getParameter("limit"));
        // 获取“任务”查询器
        List<Task> tasks = taskService.createTaskQuery().list();
        List<Assignment> assignmentList = new ArrayList<>();

        // 循环结果集
        for (Task task : tasks) {
            Assignment assignment = new Assignment();
            Integer applicationId = (Integer)taskService.getVariable(task.getId(), "applicationId");
            Application application = applicationService.getApplicationById(applicationId);
            if (application == null) {
                continue;
            }
            String starterName = userService.getUserById(application.getStarterId()).getLoginName();
            String servicecatalogPlanId = application.getServicecatalogPlanId();

            HistoricProcessInstance historicProcessInstance = historyService//与历史数据（历史表）相关的Service
                    .createHistoricProcessInstanceQuery()//创建历史流程实例查询
                    .processInstanceId(task.getProcessInstanceId())//使用流程实例ID查询
                    .singleResult();
            assignment.setStarterName(starterName);
            assignment.setProcessTime(DateUtil.dateTime2String(historicProcessInstance.getStartTime()));
            assignment.setTaskTime(DateUtil.dateTime2String(task.getCreateTime()));
            assignment.setTaskId(task.getId());
            assignment.setTaskName(task.getName());
            assignment.setProcessId(task.getProcessInstanceId());
            assignment.setAssignee(task.getAssignee());
            assignment.setServicecatalogPlanId(servicecatalogPlanId);
            assignmentList.add(assignment);
        }
        Collections.sort(assignmentList);
        if(assignmentList.size() > (start + limit)) {
            result.setData(assignmentList.subList(start, start + limit));
        } else if(assignmentList.size() > start){
            result.setData(assignmentList.subList(start, assignmentList.size()));
        }
        result.setTotal(assignmentList.size());
        outputToJSON(response, result);
    }

    /**
     * 获取任务详情
     *
     * @return 流程实例信息
     * @throws Exception
     * @param: 流程ID
     */
    @RequestMapping("/getInfo.do")
    public void getInfo(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String servicecatalogPlanId = request.getParameter("servicecatalogPlanId");
        Map<String, Object> example = getInfo(servicecatalogPlanId);
        result.setData(example);
        outputToJSON(response, result);
    }

    /**
     * 获取任务详情
     *
     * @return 流程实例信息
     * @throws Exception
     * @param: 流程ID
     */
    public Map<String, Object> getInfo(String servicecatalogPlanId) {
        Application application = applicationService.getApplicationByServicecatalogPlanId(servicecatalogPlanId);
        ProductVersion productVersion = productVersionService.getProductVersionById(application.getProductVersionId());
        Product product = productService.getProductById(application.getProductId());
        User user = userService.getUserById(application.getStarterId());
        String servicecatalogPortfolioId = userProductService.getServicecatalogPortfolioId(application.getProductId(), application.getStarterId());
        Map<String, Object> example = new HashMap<>();
        example.put("应用", Optional.ofNullable(productVersion).map(ProductVersion::getApp).orElse(null));
        example.put("环境", Optional.ofNullable(productVersion).map(ProductVersion::getEnvironment).orElse(null));
        example.put("产品ID", Optional.ofNullable(product).map(Product::getServicecatalogProductId).orElse(null));
        example.put("实例名称", application.getProvisionedProductName());
        example.put("参数信息", application.getParameters());
        example.put("地域", application.getRegion());
        example.put("版本ID", Optional.ofNullable(productVersion).map(ProductVersion::getServicecatalogProductVersionId).orElse(null));
        example.put("申请人", Optional.ofNullable(user).map(User::getLoginName).orElse(null));
        example.put("角色ID", application.getRoleId());
        example.put("产品名称", Optional.ofNullable(product).map(Product::getProductName).orElse(null));
        example.put("产品组合ID", servicecatalogPortfolioId);

        return example;
    }

    @RequestMapping("/getCount.do")
    public void getCount(HttpServletRequest request, HttpServletResponse response) {
        WebResult result = new WebResult();
        String flag = "no";
        String count = request.getParameter("count");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Task> list = taskService.createTaskQuery()//创建任务查询对象
            .taskAssignee(username)//指定个人任务查询
            .list().stream()
            .filter(task -> {
                Integer applicationId;
                try {
                    applicationId = (Integer)taskService.getVariable(task.getId(), "applicationId");
                } catch (Exception e) {
                    return false;
                }
                Application application = applicationService.getApplicationById(applicationId);
                return application != null;
            })
            .collect(Collectors.toList());
        if (StringUtils.isBlank(count)) {
            flag = "no";
        } else if (list.size() != Integer.parseInt(count)) {
            flag = "yes";
        }
        result.setSuccess(true);
        result.setData(flag);
        outputToJSON(response, result);
    }
}

