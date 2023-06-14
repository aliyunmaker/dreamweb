package cc.landingzone.dreamweb.service;

import java.util.Iterator;
import java.util.List;

import cc.landingzone.dreamweb.dao.UserRoleDao;
import cc.landingzone.dreamweb.model.enums.SSOSpEnum;
import cc.landingzone.dreamweb.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Component
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Transactional
    public void addUserRole(UserRole userRole) {
        Assert.notNull(userRole, "数据不能为空!");
        Assert.hasText(userRole.getRoleName(), "名称不能为空!");
        Assert.notNull(userRole.getRoleType(), "类型不能为空!");
        Assert.hasText(userRole.getRoleValue(), "值不能为空!");
        Assert.notNull(userRole.getUserGroupId(), "userGroupId can not be null!");
        userRoleDao.addUserRole(userRole);
    }

    @Transactional
    public void updateUserRole(UserRole userRole) {
        Assert.notNull(userRole, "数据不能为空!");
        Assert.notNull(userRole.getId(), "id不能为空!");
        Assert.hasText(userRole.getRoleName(), "名称不能为空!");
        Assert.notNull(userRole.getRoleType(), "类型不能为空!");
        Assert.hasText(userRole.getRoleValue(), "值不能为空!");
        Assert.notNull(userRole.getUserGroupId(), "userGroupId can not be null!");
        userRoleDao.updateUserRole(userRole);
    }

    public List<UserRole> getUserRolesByGroupId(Integer userGroupId) {
        Assert.notNull(userGroupId, "userGroupId can not be null!");
        return userRoleDao.getUserRolesByGroupId(userGroupId);
    }

    @Transactional
    public void deleteUserRole(Integer id) {
        Assert.notNull(id, "id can not be null!");
        userRoleDao.deleteUserRole(id);
    }

    @Transactional
    public void deleteUserRoleByUserGroupId(Integer userGroupId) {
        Assert.notNull(userGroupId, "userGroupId can not be null!");
        userRoleDao.deleteUserRoleByUserGroupId(userGroupId);
    }

    public List<UserRole> getRoleListByUserId(Integer userId) {
        Assert.notNull(userId, "userId can not be null!");
        return userRoleDao.getUserRolesByUserId(userId);
    }

    public List<UserRole> getRoleListByUserId(Integer userId, SSOSpEnum ssoSp) {
        Assert.notNull(ssoSp, "ssoSp can not be null!");
        List<UserRole> list = getRoleListByUserId(userId);
        Iterator<UserRole> iterator = list.iterator();
        while (iterator.hasNext()) {
            UserRole item = iterator.next();
            if (!ssoSp.equals(item.getRoleType())) {
                iterator.remove();
            }
        }
        return list;
    }

    public UserRole getUserRoleById(Integer userRoleId) {
        Assert.notNull(userRoleId, "userRoleName can not be null!");
        return userRoleDao.getUserRoleByName(userRoleId);
    }

}
