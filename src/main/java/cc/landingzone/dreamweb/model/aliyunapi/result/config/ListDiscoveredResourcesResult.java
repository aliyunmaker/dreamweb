package cc.landingzone.dreamweb.model.aliyunapi.result.config;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
public class ListDiscoveredResourcesResult {

    @JSONField(name = "DiscoveredResourceProfiles")
    private DiscoveredResourceProfiles discoveredResourceProfiles;

    public DiscoveredResourceProfiles getDiscoveredResourceProfiles() {
        return discoveredResourceProfiles;
    }

    public void setDiscoveredResourceProfiles(DiscoveredResourceProfiles discoveredResourceProfiles) {
        this.discoveredResourceProfiles = discoveredResourceProfiles;
    }
}
