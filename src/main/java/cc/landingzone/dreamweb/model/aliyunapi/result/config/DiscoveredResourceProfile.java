package cc.landingzone.dreamweb.model.aliyunapi.result.config;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
public class DiscoveredResourceProfile {

    @JSONField(name = "ResourceType")
    private String resourceType;
    @JSONField(name = "Region")
    private String region;
    @JSONField(name = "ResourceDeleted")
    private Integer resourceDeleted;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getResourceDeleted() {
        return resourceDeleted;
    }

    public void setResourceDeleted(Integer resourceDeleted) {
        this.resourceDeleted = resourceDeleted;
    }
}
