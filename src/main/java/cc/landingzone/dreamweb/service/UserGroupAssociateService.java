package cc.landingzone.dreamweb.service;

import java.util.List;

import cc.landingzone.dreamweb.dao.UserGroupAssociateDao;
import cc.landingzone.dreamweb.model.UserGroupAssociate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Component
public class UserGroupAssociateService {

    @Autowired
    private UserGroupAssociateDao userGroupAssociateDao;

    @Transactional
    public void addUserGroupAssociate(UserGroupAssociate userGroupAssociate) {
        Assert.notNull(userGroupAssociate, "数据不能为空!");
        Assert.notNull(userGroupAssociate.getUserGroupId(), "userGroupId不能为空!");
        Assert.notNull(userGroupAssociate.getUserId(), "userId不能为空!");
        userGroupAssociateDao.addUserGroupAssociate(userGroupAssociate);
    }

    public List<UserGroupAssociate> getUserGroupAssociatesByUserGroupId(Integer userGroupId) {
        Assert.notNull(userGroupId, "数据不能为空!");
        return userGroupAssociateDao.getUserGroupAssociatesByUserGroupId(userGroupId);
    }

    @Transactional
    public void deleteUserGroupAssociateById(Integer id) {
        Assert.notNull(id, "id can not be null!");
        userGroupAssociateDao.deleteUserGroupAssociateById(id);
    }

    @Transactional
    public void deleteUserGroupAssociateByUserGroupId(Integer userGroupId) {
        Assert.notNull(userGroupId, "userGroupId can not be null!");
        userGroupAssociateDao.deleteUserGroupAssociateByUserGroupId(userGroupId);
    }

    @Transactional
    public void deleteUserGroupAssociate(Integer userId, Integer userGroupId) {
        Assert.notNull(userId, "userId can not be null!");
        Assert.notNull(userGroupId, "userGroupId can not be null!");
        userGroupAssociateDao.deleteUserGroupAssociate(userId, userGroupId);
    }

}
