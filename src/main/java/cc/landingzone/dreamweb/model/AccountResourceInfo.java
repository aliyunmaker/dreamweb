package cc.landingzone.dreamweb.model;

/**
 * @author merc-bottle
 * @date 2021/02/04
 */
public class AccountResourceInfo {

    private String accountId;
    private String displayName;
    private String resourceCount;
    private String resourceCountDeleted;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(String resourceCount) {
        this.resourceCount = resourceCount;
    }

    public String getResourceCountDeleted() {
        return resourceCountDeleted;
    }

    public void setResourceCountDeleted(String resourceCountDeleted) {
        this.resourceCountDeleted = resourceCountDeleted;
    }
}
