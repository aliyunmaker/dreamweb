package cc.landingzone.dreamweb.model.aliyunapi.result.resourcemanager;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
public class ListAccountsResult {

    @JSONField(name = "TotalCount")
    private Integer totalCount;
    @JSONField(name = "PageSize")
    private Integer pageSize;
    @JSONField(name = "PageNumber")
    private Integer pageNumber;
    @JSONField(name = "Accounts")
    private Accounts accounts;

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

    public Accounts getAccounts() {
        return accounts;
    }

    public void setAccounts(Accounts accounts) {
        this.accounts = accounts;
    }
}
