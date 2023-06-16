package cc.landingzone.dreamweb.demo.appcenter.model;

import lombok.Data;

import java.util.Date;

@Data
public class Resource {
    private String resourceId;
    private String resourceName;
    private String environmentType;
    private String regionId;
    private Date createTime;
}
