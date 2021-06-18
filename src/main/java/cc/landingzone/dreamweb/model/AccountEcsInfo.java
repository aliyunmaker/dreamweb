package cc.landingzone.dreamweb.model;

import java.util.List;

public class AccountEcsInfo {

    private String accountId;
    private String displayName;
    private List<String> instanceIdList;

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

    public List<String> getInstanceIdList() {
        return instanceIdList;
    }

    public void setInstanceIdList(List<String> instanceList) {
        this.instanceIdList = instanceList;
    }
}
