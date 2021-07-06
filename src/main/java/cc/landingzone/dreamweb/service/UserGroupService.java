package cc.landingzone.dreamweb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.landingzone.dreamweb.dao.UserGroupDao;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Component
public class UserGroupService {

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserGroupAssociateService userGroupAssociateService;

    @Transactional
    public void addUserGroup(UserGroup userGroup) {
        Assert.notNull(userGroup, "数据不能为空!");
        Assert.hasText(userGroup.getName(), "名称不能为空!");
        UserGroup userGroupDB = getUserGroupByName(userGroup.getName());
        if (userGroupDB != null) {
            throw new IllegalArgumentException("用户组不能重名:" + userGroup.getName());
        }
        userGroupDao.addUserGroup(userGroup);
    }

    @Transactional
    public void updateUserGroup(UserGroup userGroup) {
        Assert.notNull(userGroup, "数据不能为空!");
        Assert.notNull(userGroup.getId(), "id不能为空!");
        Assert.hasText(userGroup.getName(), "名称不能为空!");
        userGroupDao.updateUserGroup(userGroup);
    }

    public List<UserGroup> getAllUserGroups() {
        return userGroupDao.getAllUserGroups();
    }

    public List<UserGroup> getUserGroupsByUserId(Integer userId) {
        Assert.notNull(userId, "用户id不能为空!");
        return userGroupDao.getUserGroupsByUserId(userId);
    }
    
    public UserGroup getUserGroupByName(String name) {
        Assert.hasText(name, "名称不能为空!");
        return userGroupDao.getUserGroupByName(name);
    }

    public List<UserGroup> getUserGroupsByNames(List<String> userGroupNames) {
        Assert.notEmpty(userGroupNames, "用户组名不能为空!");
        List<UserGroup> userGroups = userGroupDao.getUserGroupsByNames(userGroupNames);
        if (userGroups.size() != userGroupNames.size()) {
            List<String> diff = new ArrayList<String>();
            Map<String, Integer> map = new HashMap<String, Integer>(userGroupNames.size());
            for (UserGroup userGroup : userGroups) {
                map.put(userGroup.getName(), 1);
            }
            for (String name : userGroupNames) {
                if (map.get(name) == null) {
                    diff.add(name);
                }
            }
            Assert.isTrue(userGroups.size() == userGroupNames.size(), "操作失败！以下用户组不存在：" + String.join(",", diff));
        }
        return userGroups;
    }

    @Transactional
    public void deleteUserGroup(Integer id) {
        Assert.notNull(id, "id can not be null!");
        userGroupDao.deleteUserGroup(id);

        // 删除role和用户组关联
        userRoleService.deleteUserRoleByUserGroupId(id);
        userGroupAssociateService.deleteUserGroupAssociateByUserGroupId(id);
    }

    public List<User> getUsersByUserGroupId(Integer userGroupId) {
        Assert.notNull(userGroupId, "userGroupId can not be null!");
        return userGroupDao.getUsersByUserGroupId(userGroupId);
    }

}
