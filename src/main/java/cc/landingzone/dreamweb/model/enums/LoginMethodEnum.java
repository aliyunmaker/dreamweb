package cc.landingzone.dreamweb.model.enums;

/**
 * @author merc-bottle
 * @date 2021/02/07
 */
public enum LoginMethodEnum {

    /**
     * 用户名密码登录
     */
    NORMAL_LOGIN("login from website by username and password"),

    /**
     * LDAP用户
     */
    LDAP_LOGIN("login form website by ldap"),

    /**
     * 微信登录
     */
    WEIXIN_LOGIN("login from website by weixin qrcode"),
    /**
     * 通过token的方式自动登录
     */
    AUTO_LOGIN("auto login by token");


    private String comment;

    LoginMethodEnum(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
