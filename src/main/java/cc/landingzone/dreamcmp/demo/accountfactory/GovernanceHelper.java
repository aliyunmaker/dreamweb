package cc.landingzone.dreamcmp.demo.accountfactory;

import cc.landingzone.dreamcmp.common.ClientHelper;
import cc.landingzone.dreamcmp.common.CommonConstants;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.governance20210120.Client;
import com.aliyun.governance20210120.models.GetAccountFactoryBaselineResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：珈贺
 * Description：
 */
public class GovernanceHelper {
    public static Logger logger = LoggerFactory.getLogger(GovernanceHelper.class);

    public static List<com.aliyun.governance20210120.models.ListAccountFactoryBaselinesResponseBody.ListAccountFactoryBaselinesResponseBodyBaselines> listAccountFactoryBaselines() throws Exception{
        Client client = ClientHelper.createGovernanceClient(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);
        com.aliyun.governance20210120.models.ListAccountFactoryBaselinesRequest listAccountFactoryBaselinesRequest = new
                com.aliyun.governance20210120.models.ListAccountFactoryBaselinesRequest();
        RuntimeOptions runtime = new RuntimeOptions();
        return client.listAccountFactoryBaselinesWithOptions(listAccountFactoryBaselinesRequest, runtime).getBody().getBaselines();
    }

    public static JSONObject getAccountFactoryBaseline(String baselineId) throws Exception{
        JSONObject baselineDetail = new JSONObject();

        Client client = ClientHelper.createGovernanceClient(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);
        com.aliyun.governance20210120.models.GetAccountFactoryBaselineRequest getAccountFactoryBaselineRequest = new
                com.aliyun.governance20210120.models.GetAccountFactoryBaselineRequest()
                .setBaselineId(baselineId);
        RuntimeOptions runtime = new RuntimeOptions();
        GetAccountFactoryBaselineResponseBody body = client.getAccountFactoryBaselineWithOptions(getAccountFactoryBaselineRequest, runtime).getBody();

        baselineDetail.put("id", body.getBaselineId());
        baselineDetail.put("name", body.getBaselineName());

        List<JSONObject> baselineItems = new ArrayList<>();
        for (GetAccountFactoryBaselineResponseBody.GetAccountFactoryBaselineResponseBodyBaselineItems item: body.getBaselineItems()) {
            JSONObject baselineItem = new JSONObject();
            baselineItem.put("itemName", item.getName());
            JSONObject config = JSONObject.parseObject(item.getConfig());
            baselineItem.put("config", config);

            baselineItems.add(baselineItem);
        }
        baselineDetail.put("baselineItems", baselineItems);
        return baselineDetail;
    }

    public static Long enrollAccount(String accountNamePrefix, String displayName, String folderId,
                                     Long payerAccountUid, String baselineId ) throws Exception {
        Client client = ClientHelper.createGovernanceClient(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);
        com.aliyun.governance20210120.models.EnrollAccountRequest enrollAccountRequest = new
                com.aliyun.governance20210120.models.EnrollAccountRequest()
                .setAccountNamePrefix(accountNamePrefix)
                .setDisplayName(displayName)
                .setFolderId(folderId)
//                .setPayerAccountUid(payerAccountUid)
//                .setAccountUid(accountUid)
                .setBaselineId(baselineId);
//                .setResellAccountType(resellAccountType);
        if (payerAccountUid != null) {
            enrollAccountRequest.setPayerAccountUid(payerAccountUid);
        }
        RuntimeOptions runtime = new RuntimeOptions();
        return client.enrollAccountWithOptions(enrollAccountRequest, runtime).getBody().getAccountUid();
    }

}
