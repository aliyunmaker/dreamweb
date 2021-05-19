package cc.landingzone.dreamweb.dao;

import java.util.List;

import cc.landingzone.dreamweb.model.UserGroupAssociate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


@Component
public interface UserGroupAssociateDao {

    void addUserGroupAssociate(UserGroupAssociate userGroupAssociate);

    void addUserGroupAssociates(List<UserGroupAssociate> userGroupAssociates);

    List<UserGroupAssociate> getUserGroupAssociatesByUserGroupId(Integer userGroupId);

    void deleteUserGroupAssociateById(Integer id);

    void deleteUserGroupAssociate(@Param(value = "userId") Integer userId,
                                  @Param(value = "userGroupId") Integer userGroupId);

    void deleteUserGroupAssociates(List<UserGroupAssociate> userGroupAssociates);

    void deleteUserGroupAssociateByUserGroupId(Integer userGroupId);
}
