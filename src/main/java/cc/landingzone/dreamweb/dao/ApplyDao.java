package cc.landingzone.dreamweb.dao;

import cc.landingzone.dreamweb.model.Apply;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ApplyDao {

    void addApply(Apply apply);

    List<Apply> getApply(String startername);

    void updateTask(String processid, String task);

    void updateProcessState(String processid, String processstate);

    void updateCond(String processid, String cond);

    void updateOpinion(String processid, String opinion);
}
