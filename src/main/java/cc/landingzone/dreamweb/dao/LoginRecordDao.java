package cc.landingzone.dreamweb.dao;

import java.util.List;
import java.util.Map;

import cc.landingzone.dreamweb.model.LoginRecord;
import org.springframework.stereotype.Component;

/**
 * @author merc-bottle
 * @date 2021/02/07
 */
@Component
public interface LoginRecordDao {

    void addLoginRecord(LoginRecord loginRecord);

    List<LoginRecord> listLoginRecord(Map<String, Object> map);

    Integer countLoginRecord(Map<String, Object> map);
}
