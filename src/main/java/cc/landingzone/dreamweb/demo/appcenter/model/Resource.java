package cc.landingzone.dreamweb.demo.appcenter.model;

import lombok.Data;

import java.util.Map;

@Data
public class Resource {
    private String serviceName;
    private String resourceId;
    private String resourceName;
    private String resourceType;
    private String environmentType;
    private String regionId;
    private String createTime;
    private Map<String, String> operations;
}
