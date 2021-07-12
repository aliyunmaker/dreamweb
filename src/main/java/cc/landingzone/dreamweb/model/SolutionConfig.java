package cc.landingzone.dreamweb.model;

import java.util.Date;

public class SolutionConfig {

    private Integer id;
    private String name;
    private String intro;
    private String webConfig;
    private String creator;
    private String module;
    private Integer customerNum;
    private Boolean isMVP;
    private Date gmtCreate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getWebConfig() {
        return webConfig;
    }

    public void setWebConfig(String webConfig) {
        this.webConfig = webConfig;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
    
    public Integer getCustomerNum() {
        return customerNum;
    }

    public void setCustomerNum(Integer customerNum) {
        this.customerNum = customerNum;
    }

    public Boolean getIsMVP() {
        return isMVP;
    }

    public void setIsMVP(Boolean isMVP) {
        this.isMVP = isMVP;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

}
