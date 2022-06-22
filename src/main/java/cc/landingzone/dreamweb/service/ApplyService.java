package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ApplyDao;
import cc.landingzone.dreamweb.model.Apply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public void saveApply(Apply apply) {
        applyDao.saveApply(apply);
    }

    @Transactional
    public List<Apply> listApply(String starterName) {
        return applyDao.listApply(starterName);
    }

    @Transactional
    public void updateTask(String processId, String task) {
        applyDao.updateTask(processId, task);
    }

    @Transactional
    public void updateProcessState(String processId, String processState) {
        applyDao.updateProcessState(processId, processState);
    }

    @Transactional
    public void updateCond(String processId, String cond) {
        applyDao.updateCond(processId, cond);
    }

    @Transactional
    public void updateOpinion(String processId, String opinion) {
        applyDao.updateOpinion(processId, opinion);
    }

}
