package cc.landingzone.dreamweb.model;

import java.util.Date;

/**
 * @author merc-bottle
 * @date 2021/02/07
 */
public class LoginRecord {

    private Integer id;
    private String clientIpAddr;
    private String loginName;
    private String loginMethod;
    private String comment;
    private Date gmtCreate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClientIpAddr() {
        return clientIpAddr;
    }

    public void setClientIpAddr(String clientIpAddr) {
        this.clientIpAddr = clientIpAddr;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}
