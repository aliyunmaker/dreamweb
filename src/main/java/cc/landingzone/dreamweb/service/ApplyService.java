package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.dao.ApplyDao;
import cc.landingzone.dreamweb.model.Apply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ApplyService {

    @Autowired
    private ApplyDao applyDao;

    @Transactional
    public void addApply(Apply apply) {
        applyDao.addApply(apply);
    }

    @Transactional
    public List<Apply> getApply(String startername) {
        return applyDao.getApply(startername);
    }

    @Transactional
    public void updateTask(String processid, String task) {
        applyDao.updateTask(processid, task);
    }

    @Transactional
    public void updateProcessState(String processid, String processstate) {
        applyDao.updateProcessState(processid, processstate);
    }

    @Transactional
    public void updateCond(String processid, String cond) {
        applyDao.updateCond(processid, cond);
    }

    @Transactional
    public void updateOpinion(String processid, String opinion) {
        applyDao.updateOpinion(processid, opinion);
    }

}
