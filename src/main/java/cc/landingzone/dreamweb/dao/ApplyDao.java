package cc.landingzone.dreamweb.dao;

import cc.landingzone.dreamweb.model.Apply;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 操作工作流申请
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Component
public interface ApplyDao {

    void saveApply(Apply apply);

    List<Apply> listApply(String starterName);

    void updateTask(String processId, String task);

    void updateProcessState(String processId, String processState);

    void updateCond(String processId, String cond);

    void updateOpinion(String processId, String opinion);
}
