package cc.landingzone.dreamweb.model.aliyunapi.result.config;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
public class DiscoveredResourceProfiles {

    @JSONField(name = "TotalCount")
    private Integer totalCount;
    @JSONField(name = "PageSize")
    private Integer pageSize;
    @JSONField(name = "PageNumber")
    private Integer pageNumber;
    @JSONField(name = "DiscoveredResourceProfileList")
    private List<DiscoveredResourceProfile> discoveredResourceProfileList;

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<DiscoveredResourceProfile> getDiscoveredResourceProfileList() {
        return discoveredResourceProfileList;
    }

    public void setDiscoveredResourceProfileList(List<DiscoveredResourceProfile> discoveredResourceProfileList) {
        this.discoveredResourceProfileList = discoveredResourceProfileList;
    }
}
