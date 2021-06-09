package cc.landingzone.dreamweb.model;

import java.util.Date;

public class SlsConfig {
    private Integer id;
    private String configName;
    private String configValue;
    private Integer configOwnerId;
    private String comment;
    private Date gmtCreate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public Integer getConfigOwnerId() {
        return configOwnerId;
    }

    public void setConfigOwnerId(Integer configOwnerId) {
        this.configOwnerId = configOwnerId;
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
