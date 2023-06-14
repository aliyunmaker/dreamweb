package cc.landingzone.dreamweb.dao;

import java.util.List;

import cc.landingzone.dreamweb.model.ApiUser;
import org.springframework.stereotype.Component;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
@Component
public interface ApiUserDao {

    void addApiUser(ApiUser apiUser);

    void updateApiUser(ApiUser apiUser);

    ApiUser getApiUserById(Integer id);

    ApiUser getApiUserByAccessKeyId(String accessKeyId);

    List<ApiUser> listApiUser();

    void deleteApiUser(Integer id);
}
