package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ApplyDao;
import cc.landingzone.dreamweb.model.Apply;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import com.aliyun.servicecatalog20210901.Client;
import com.aliyun.servicecatalog20210901.models.GetProvisionedProductPlanRequest;
import com.aliyun.servicecatalog20210901.models.GetProvisionedProductPlanResponse;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流申请相关操作
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Component
public class ApplyService {

    @Autowired
    private ApplyDao applyDao;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ServiceCatalogViewService serviceCatalogViewService;

    private static Logger logger = LoggerFactory.getLogger(ServiceCatalogViewService.class);

    @Transactional
    public void saveApply(Apply apply) {
        applyDao.saveApply(apply);
    }

    @Transactional
    public List<Apply> listApply(String starterName) {
        return applyDao.listApply(starterName);
    }

    @Transactional
    public List<Apply> listApplyPreviewInProgress() {return applyDao.listApplyPreviewInProgress(); }

    @Transactional
    public void updateTaskByProcessId(String processId, String task) {
        applyDao.updateTaskByProcessId(processId, task);
    }

    @Transactional
    public void updateProcessState(String processId, String processState) {
        applyDao.updateProcessState(processId, processState);
    }

    @Transactional
    public Apply getApplyByPlanId(String planId) { return applyDao.getApplyByPlanId(planId);}

    @Transactional
    public void updateCond(String processId, String cond) {
        applyDao.updateCond(processId, cond);
    }

    @Transactional
    public void updateOpinion(String processId, String opinion) {
        applyDao.updateOpinion(processId, opinion);
    }

    @Transactional
    public void updateProcessIdByPlanId(String planId, String processId) { applyDao.updateProcessIdByPlanId(planId, processId);}

    @Transactional
    public void updateTaskByPlanId(String planId, String task) { applyDao.updateTaskByPlanId(planId, task);}

    /**
     * 预检通过启动流程实例
     *
     * @return
     * @throws Exception
     * @param:
     */
    public void startProcessByDefinitionId(Apply apply) {

        // 启动流程
        String userName = apply.getStarterName();
        identityService.setAuthenticatedUserId(userName);
        Map<String, Object> variables = new HashMap<>();
        variables.put("parameters", apply.getParameters());
        variables.put("starterName", userName);
        variables.put("application", apply.getApplication());
        variables.put("scene", apply.getScene());
        variables.put("productId", apply.getProductId());
        variables.put("exampleName", apply.getExampleName());
        variables.put("roleId", apply.getRoleId());
        variables.put("region", apply.getRegion());
        variables.put("versionId", apply.getVersionId());
        variables.put("planId", apply.getPlanId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(apply.getProcessDefinitionId(), variables);
        String task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult().getName();

        updateProcessIdByPlanId(apply.getPlanId(), processInstance.getProcessInstanceId());
        updateTaskByPlanId(apply.getPlanId(), "等待" + task);

    }

    @Scheduled(cron = "0/3 * * * * ?")
    public void updateProcess() {
        try {
            List<Apply> applys = listApplyPreviewInProgress(); //查询流程表中"预检中"状态的记录
            if (applys != null) {
                for (Apply apply : applys) {
                    // 更新状态
                    // 创建终端
                    String region = "cn-hangzhou";
                    String userName = apply.getStarterName();
                    Integer roleId = apply.getRoleId();
                    User user = userService.getUserByLoginName(userName);
                    UserRole userRole = userRoleService.getUserRoleById(roleId);
                    Client client = serviceCatalogViewService.createClient(region, user, userRole, apply.getProductId());
                    // 查询并更新数据库，还是调用getProvisionedProduct和getTask接口
                    GetProvisionedProductPlanRequest request1 = new GetProvisionedProductPlanRequest();
                    request1.setPlanId(apply.getPlanId());
                    GetProvisionedProductPlanResponse response1 = client.getProvisionedProductPlan(request1);
                    if(response1.getBody().getPlanDetail().status.equals("PreviewSuccess")) {
                        applyDao.updateStatusByPlanId(apply.getPlanId(), "审批中");
                        applyDao.updatePlanResultByPlanId(apply.getPlanId(), "预检通过");
                        startProcessByDefinitionId(apply);
                    } else if(response1.getBody().getPlanDetail().status.equals("PreviewFailed")) {
                        applyDao.updateStatusByPlanId(apply.getPlanId(), "预检失败");
                        applyDao.updatePlanResultByPlanId(apply.getPlanId(), response1.getBody().getPlanDetail().statusMessage);
                    } else {
                        System.out.println(response1.getBody().getPlanDetail().status);
                    }
                    // 如果状态改变，修改数据库为预检失败 or 修改数据库为预检成功且开启工作流审批
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
