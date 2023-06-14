package cc.landingzone.dreamweb.model.aliyunapi.result.resourcemanager;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author merc-bottle
 * @date 2021/02/02
 */
public class Accounts {

    @JSONField(name = "Account")
    private List<Account> account;

    public List<Account> getAccount() {
        return account;
    }

    public void setAccount(List<Account> account) {
        this.account = account;
    }
}
