package cc.landingzone.dreamcmp.demo.dailyinspection;

import cc.landingzone.dreamcmp.common.ClientHelper;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.demo.dailyinspection.model.Rule;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.config20200907.models.ListConfigRulesResponseBody;

import java.util.ArrayList;
import java.util.List;


public class DailyInspectionUtil {
    public static List<Rule> listRules() throws Exception {
        List<Rule> rules = new ArrayList<>();

        com.aliyun.config20200907.Client client = ClientHelper.createConfigClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.config20200907.models.ListConfigRulesRequest listConfigRulesRequest = new com.aliyun.config20200907.models.ListConfigRulesRequest()
                .setKeyword(CommonConstants.CONFIG_RULE_PREFIX);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        List<ListConfigRulesResponseBody.ListConfigRulesResponseBodyConfigRulesConfigRuleList> configRules = client.listConfigRulesWithOptions(listConfigRulesRequest, runtime).getBody().getConfigRules().getConfigRuleList();

        for (ListConfigRulesResponseBody.ListConfigRulesResponseBodyConfigRulesConfigRuleList configRule: configRules) {
            Rule rule = new Rule();
            rule.setId(configRule.getConfigRuleId());
            rule.setName(configRule.getConfigRuleName());
            JSONObject compliance = new JSONObject();
            compliance.put("complianceType", configRule.getCompliance().getComplianceType());
            compliance.put("count", configRule.getCompliance().getCount());
            rule.setCompliance(compliance);
            rule.setState(configRule.getConfigRuleState());
            rules.add(rule);
        }

        return rules;
    }

    public static String activateRules(String ruleIds) throws Exception {
//        com.aliyun.config20200907.Client client = ClientHelper.createConfigClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
//        com.aliyun.config20200907.models.ActiveConfigRulesRequest activeConfigRulesRequest = new com.aliyun.config20200907.models.ActiveConfigRulesRequest()
//                .setConfigRuleIds(ruleIds);
//        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
//
//        return client.activeConfigRulesWithOptions(activeConfigRulesRequest, runtime).getBody().getRequestId();
        return null;
    }

    public static String deactivateRules(String ruleIds) throws Exception {
        com.aliyun.config20200907.Client client = ClientHelper.createConfigClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.config20200907.models.DeactiveConfigRulesRequest deactiveConfigRulesRequest = new com.aliyun.config20200907.models.DeactiveConfigRulesRequest()
                .setConfigRuleIds(ruleIds);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        return client.deactiveConfigRulesWithOptions(deactiveConfigRulesRequest, runtime).getBody().getRequestId();
    }

    public static JSONObject getRule() {
        return new JSONObject();
    }
}
