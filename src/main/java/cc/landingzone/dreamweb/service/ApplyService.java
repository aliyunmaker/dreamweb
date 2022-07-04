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

}
