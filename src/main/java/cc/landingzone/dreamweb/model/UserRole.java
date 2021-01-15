package cc.landingzone.dreamweb.model;

/**
 * 角色,目前只支持跟用户组关联
 *
 * @author charles
 * @date 2020-10-13
 */
public class UserRole implements Comparable<UserRole> {

    private Integer id;
    private Integer userGroupId;
    private SSOSpEnum roleType;
    private String roleName;
    private String roleValue;

    @Override
    public String toString() {
        return "[" + roleType + "][" + roleName + "][" + roleValue + "]";
    }

    @Override
    public int compareTo(UserRole o) {
        if (o == null || o.getRoleType() == null) {
            return 1;
        }
        if (this.roleType == null) {
            return -1;
        }
        return roleType.ordinal() - o.getRoleType().ordinal();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserGroupId() {
        return userGroupId;
    }

    public void setUserGroupId(Integer userGroupId) {
        this.userGroupId = userGroupId;
    }

    public SSOSpEnum getRoleType() {
        return roleType;
    }

    public void setRoleType(SSOSpEnum roleType) {
        this.roleType = roleType;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleValue() {
        return roleValue;
    }

    public void setRoleValue(String roleValue) {
        this.roleValue = roleValue;
    }

}
