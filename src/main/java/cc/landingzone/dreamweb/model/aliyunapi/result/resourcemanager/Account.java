package cc.landingzone.dreamweb.model.aliyunapi.result.resourcemanager;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
public class Account {

    @JSONField(name = "AccountId")
    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
