package cc.landingzone.dreamweb.dao;

import java.util.List;
import java.util.Map;

import cc.landingzone.dreamweb.model.User;
import org.springframework.stereotype.Component;

@Component
public interface UserDao {

    void addUser(User user);

    void updateUser(User user);

    User getUserByLoginName(String loginName);

    User getUserByUnionid(String unionid);

    User getUserById(Integer id);

    void deleteUser(Integer id);

    List<User> searchUser(Map<String, Object> map);

    List<User> getUsersByLoginMethod(Map<String, Object> map);

    Integer searchUserTotal(Map<String, Object> map);

}
