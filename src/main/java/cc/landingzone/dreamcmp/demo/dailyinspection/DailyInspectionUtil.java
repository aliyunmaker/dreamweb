package cc.landingzone.dreamcmp.demo.dailyinspection;

import cc.landingzone.dreamcmp.common.ClientHelper;
import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.demo.dailyinspection.model.Resource;
import cc.landingzone.dreamcmp.demo.dailyinspection.model.Rule;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.config20200907.models.GetConfigRuleResponseBody;
import com.aliyun.config20200907.models.ListConfigRuleEvaluationResultsResponseBody;
import com.aliyun.config20200907.models.ListConfigRulesResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
            // name和pillar是用","分隔开的
            String[] configRuleName = configRule.getConfigRuleName().split(",");
            rule.setId(configRule.getConfigRuleId());
            // 去掉前缀dreamcmp
            rule.setName(configRuleName[0].split("-")[1]);
            rule.setPillar(configRuleName[1]);
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
        com.aliyun.config20200907.Client client = ClientHelper.createConfigClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.config20200907.models.ActiveConfigRulesRequest activeConfigRulesRequest = new com.aliyun.config20200907.models.ActiveConfigRulesRequest()
                .setConfigRuleIds(ruleIds);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        return client.activeConfigRulesWithOptions(activeConfigRulesRequest, runtime).getBody().getRequestId();
    }

    public static String deactivateRules(String ruleIds) throws Exception {
        com.aliyun.config20200907.Client client = ClientHelper.createConfigClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.config20200907.models.DeactiveConfigRulesRequest deactiveConfigRulesRequest = new com.aliyun.config20200907.models.DeactiveConfigRulesRequest()
                .setConfigRuleIds(ruleIds);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        return client.deactiveConfigRulesWithOptions(deactiveConfigRulesRequest, runtime).getBody().getRequestId();
    }

    public static JSONObject getRule(String ruleId) throws Exception {
        JSONObject ruleDetail = new JSONObject();

        /* basic attrs of the rule */
        GetConfigRuleResponseBody.GetConfigRuleResponseBodyConfigRule rule = getBasicAttrsByRuleId(ruleId);

        // name和pillar是用","分隔开的
        String[] configRuleName = rule.getConfigRuleName().split(",");

        HashMap<String, String> basicAttrs = new HashMap<>();
        basicAttrs.put("id", rule.getConfigRuleId());
        // 去掉前缀dreamcmp
        basicAttrs.put("name", configRuleName[0].split("-")[1]);
        basicAttrs.put("pillar", configRuleName[1]);
        basicAttrs.put("createTime", new Date(rule.getCreateTimestamp()).toString());
        basicAttrs.put("riskLevel", rule.getRiskLevel().toString());
        basicAttrs.put("description", rule.getDescription());

        ruleDetail.put("basicAttrs", basicAttrs);

        /* inspection result of the rule */
        List<com.aliyun.config20200907.models.ListConfigRuleEvaluationResultsResponseBody
                .ListConfigRuleEvaluationResultsResponseBodyEvaluationResultsEvaluationResultList> resultList = getInspectionResultByRuleId(ruleId);

        ArrayList<Resource> inspectionResult = new ArrayList<>();
        for (ListConfigRuleEvaluationResultsResponseBody.ListConfigRuleEvaluationResultsResponseBodyEvaluationResultsEvaluationResultList result: resultList) {
            Resource resource = new Resource();
            resource.setId(result.getEvaluationResultIdentifier().getEvaluationResultQualifier().getResourceId());
            resource.setName(result.getEvaluationResultIdentifier().getEvaluationResultQualifier().getResourceName());
            resource.setResourceType(result.getEvaluationResultIdentifier().getEvaluationResultQualifier().getResourceType());
            resource.setCompliance(result.getComplianceType());
            resource.setRegionId(result.getEvaluationResultIdentifier().getEvaluationResultQualifier().getRegionId());
            inspectionResult.add(resource);
        }

        ruleDetail.put("inspectionResult", inspectionResult);

        return ruleDetail;
    }

    private static GetConfigRuleResponseBody.GetConfigRuleResponseBodyConfigRule getBasicAttrsByRuleId(String ruleId) throws Exception {
        com.aliyun.config20200907.Client client = ClientHelper.createConfigClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.config20200907.models.GetConfigRuleRequest getConfigRuleRequest = new com.aliyun.config20200907.models.GetConfigRuleRequest()
                .setConfigRuleId(ruleId);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        return client.getConfigRuleWithOptions(getConfigRuleRequest, runtime).getBody().getConfigRule();
    }

    private static List<com.aliyun.config20200907.models.ListConfigRuleEvaluationResultsResponseBody
            .ListConfigRuleEvaluationResultsResponseBodyEvaluationResultsEvaluationResultList> getInspectionResultByRuleId(String ruleId) throws Exception {
        com.aliyun.config20200907.Client client = ClientHelper.createConfigClient(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        com.aliyun.config20200907.models.ListConfigRuleEvaluationResultsRequest listConfigRuleEvaluationResultsRequest = new com.aliyun.config20200907.models.ListConfigRuleEvaluationResultsRequest()
                .setConfigRuleId(ruleId)
                .setMaxResults(100);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        return client.listConfigRuleEvaluationResultsWithOptions(listConfigRuleEvaluationResultsRequest, runtime).getBody().getEvaluationResults().getEvaluationResultList();
    }
}
