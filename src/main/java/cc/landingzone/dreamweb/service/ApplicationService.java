package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ApplicationDao;
import cc.landingzone.dreamweb.model.*;
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
 */
@Component
public class ApplicationService {

    @Autowired
    private ApplicationDao applicationDao;

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

    @Autowired
    private ProductService productService;

    private static Logger logger = LoggerFactory.getLogger(ServiceCatalogViewService.class);

    @Transactional
    public void saveApplication(Application application) {
        applicationDao.saveApplication(application);
    }

    @Transactional
    public List<Application> listApplicationsByStarterId(Integer starterId, Page page) {
        Map<String, Object> map = new HashMap<>();
        map.put("page", page);
        map.put("starterId", starterId);
        Integer total = applicationDao.searchApplicationsByStarterIdTotal(map);
        page.setTotal(total);
        return applicationDao.listApplicationsByStarterId(map);
    }

    @Transactional
    public List<Application> listApplicationPreviewInProgress() {return applicationDao.listApplicationPreviewInProgress();}

    @Transactional
    public void updateTaskByProcessId(String processId, String task) {
        applicationDao.updateTaskByProcessId(processId, task);
    }

    @Transactional
    public void updateProcessStateByProcessId(String processId, String processState) {
        applicationDao.updateProcessStateByProcessId(processId, processState);
    }

    @Transactional
    public Application getApplicationByServicecatalogPlanId(
        String servicecatalogPlanId) {return applicationDao.getApplicationByServicecatalogPlanId(servicecatalogPlanId);}

    @Transactional
    public Application getApplicationById(Integer id) {
        return applicationDao.getApplicationById(id);
    }

    @Transactional
    public void updateCondByProcessId(String processId, String cond) {
        applicationDao.updateCondByProcessId(processId, cond);
    }

    @Transactional
    public void updateOpinionByProcessId(String processId, String opinion) {
        applicationDao.updateOpinionByProcessId(processId, opinion);
    }

    @Transactional
    public void updateProcessIdById(Integer id, String processId) {applicationDao.updateProcessIdById(id, processId);}

    @Transactional
    public void updateTaskById(Integer id, String task) {applicationDao.updateTaskById(id, task);}

    /**
     * 预检通过启动流程实例
     *
     * @return
     * @throws Exception
     * @param:
     */
    public void startProcessByDefinitionId(Application application) {

        // 启动流程
        User user = userService.getUserById(application.getStarterId());
        identityService.setAuthenticatedUserId(user.getLoginName());
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicationId", application.getId());
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(application.getProcessDefinitionId(),
            variables);
        String task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
            .singleResult().getName();

        updateProcessIdById(application.getId(), processInstance.getProcessInstanceId());
        updateTaskById(application.getId(), "等待" + task);

    }

    @Scheduled(cron = "0/3 * * * * ?")
    public void updateProcess() {
        try {
            List<Application> applications = listApplicationPreviewInProgress(); //查询流程表中"预检中"状态的记录
            if (applications != null) {
                for (Application application : applications) {
                    // 更新状态
                    // 创建终端
                    String region = "cn-hangzhou";
                    Integer roleId = application.getRoleId();
                    User user = userService.getUserById(application.getStarterId());
                    UserRole userRole = userRoleService.getUserRoleById(roleId);
                    Product product = productService.getProductById(application.getProductId());
                    Client client = serviceCatalogViewService.createClient(region, user, userRole,
                        product.getServicecatalogProductId());
                    // 查询并更新数据库，还是调用getProvisionedProduct和getTask接口
                    GetProvisionedProductPlanRequest request1 = new GetProvisionedProductPlanRequest();
                    request1.setPlanId(application.getServicecatalogPlanId());
                    GetProvisionedProductPlanResponse response1 = client.getProvisionedProductPlan(request1);
                    if (response1.getBody().getPlanDetail().status.equals("PreviewSuccess")) {
                        applicationDao.updateStatusById(application.getId(), "审批中");
                        applicationDao.updatePlanResultById(application.getId(), "预检通过");
                        startProcessByDefinitionId(application);
                    } else if (response1.getBody().getPlanDetail().status.equals("PreviewFailed")) {
                        applicationDao.updateStatusById(application.getId(), "预检失败");
                        applicationDao.updatePlanResultById(application.getId(),
                            response1.getBody().getPlanDetail().statusMessage);
                    }
                    // 如果状态改变，修改数据库为预检失败 or 修改数据库为预检成功且开启工作流审批
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
