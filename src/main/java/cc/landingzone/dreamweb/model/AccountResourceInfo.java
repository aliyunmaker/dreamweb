package cc.landingzone.dreamweb.model;

/**
 * @author merc-bottle
 * @date 2021/02/04
 */
public class AccountResourceInfo {

    private String accountId;
    private String resourceCount;

    public AccountResourceInfo() {
    }

    public AccountResourceInfo(String accountId, String resourceCount) {
        this.accountId = accountId;
        this.resourceCount = resourceCount;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(String resourceCount) {
        this.resourceCount = resourceCount;
    }
}
