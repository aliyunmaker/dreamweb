package cc.landingzone.dreamweb.dao;

import java.util.List;

import cc.landingzone.dreamweb.model.UserRole;
import org.springframework.stereotype.Component;

@Component
public interface UserRoleDao {

    void addUserRole(UserRole userRole);

    void updateUserRole(UserRole userRole);

    List<UserRole> getUserRolesByGroupId(Integer userGroupId);

    List<UserRole> getUserRolesByUserId(Integer userId);

    void deleteUserRole(Integer id);

    void deleteUserRoleByUserGroupId(Integer userGroupId);

    UserRole getUserRoleByName(Integer userRoleId);

    void saveUserRole(String userName, Integer roleId);

    Integer getRoleIdByUserName(String userName);

    void updateUserRole2(String userName, Integer roleId);

}
