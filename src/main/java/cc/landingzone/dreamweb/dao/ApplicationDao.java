package cc.landingzone.dreamweb.dao;

import cc.landingzone.dreamweb.model.Application;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 操作工作流申请
 *
 * @author: laodou
 * @createDate: 2022/6/21
 */
@Component
public interface ApplicationDao {

    void saveApplication(Application application);

    List<Application> listApplicationsByStarterId(Map<String, Object> map);

    Integer searchApplicationsByStarterIdTotal(Map<String, Object> map);

    void updateTaskByProcessId(String processId, String task);

    void updateProcessStateByProcessId(String processId, String processState);

    void updateCondByProcessId(String processId, String cond);

    void updateOpinionByProcessId(String processId, String opinion);

    List<Application> listApplicationPreviewInProgress();

    void updateStatusById(Integer id, String processStatus);

    void updateProcessIdById(Integer id, String processId);

    void updateTaskById(Integer id, String task);

    Application getApplicationByServicecatalogPlanId(String servicecatalogPlanId);

    Application getApplicationById(Integer id);

    void updatePlanResultById(Integer id, String planResult);
}
