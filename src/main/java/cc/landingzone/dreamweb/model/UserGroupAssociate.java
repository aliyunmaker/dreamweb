package cc.landingzone.dreamweb.model;

/**
 * 用来关联user和userGroup,为了不侵入原来代码,独立成一个类
 *
 * @author charles
 * @date 2020-10-13
 */
public class UserGroupAssociate {
    private Integer id;
    private Integer userId;
    private Integer userGroupId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }

}
