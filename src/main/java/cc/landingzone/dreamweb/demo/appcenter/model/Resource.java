package cc.landingzone.dreamweb.demo.appcenter.model;

import lombok.Data;

@Data
public class Resource {
    private String resourceId;
    private String resourceName;
    private String environmentType;
    private String regionId;
    private String createTime;
}
