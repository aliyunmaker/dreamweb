package cc.landingzone.dreamweb.dao;

import java.util.List;

import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserGroup;
import org.springframework.stereotype.Component;


@Component
public interface UserGroupDao {

    public void addUserGroup(UserGroup userGroup);

    public List<UserGroup> getAllUserGroups();

    public void deleteUserGroup(Integer id);

    public List<User> getUsersByUserGroupId(Integer userGroupId);

    public void updateUserGroup(UserGroup userGroup);
    
    public UserGroup getUserGroupByName(String name);

}
