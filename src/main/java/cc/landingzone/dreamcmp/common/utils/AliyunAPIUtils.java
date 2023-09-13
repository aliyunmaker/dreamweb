package cc.landingzone.dreamcmp.common.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.BasicCredentials;
import com.aliyuncs.auth.BasicSessionCredentials;
import com.aliyuncs.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;

/**
 * 构建aliyun API 的client
 *
 * @author charles
 *
 */
public class AliyunAPIUtils {

    private static Logger logger = LoggerFactory.getLogger(AliyunAPIUtils.class);

    public static final String Region_Hangzhou = "cn-hangzhou";
    public static final String Region_Hongkong = "cn-hongkong";
    public static final String Region_Shanghai = "cn-shanghai";

    public static final String Policy_ECS_FULL =
        "{\"Version\":\"1\",\"Statement\":[{\"Action\":\"ecs:*\",\"Resource\":\"*\",\"Effect\":\"Allow\"},{\"Action\":[\"vpc:DescribeVpcs\",\"vpc:DescribeVSwitches\"],\"Resource\":\"*\",\"Effect\":\"Allow\"}]}";
    public static final String Policy_SLS_FULL =
        "{\"Version\":\"1\",\"Statement\":[{\"Action\":\"log:*\",\"Resource\":\"*\",\"Effect\":\"Allow\"}]}";
    public static final String Policy_ADMIN_FULL =
        "{\"Version\":\"1\",\"Statement\":[{\"Action\":\"*\",\"Resource\":\"*\",\"Effect\":\"Allow\"}]}";

    /**
     * 通用api call
     *
     * @param client
     * @param domain
     * @param version
     * @param action
     * @param params
     * @throws Exception
     */
    public static String commonInvoke(IAcsClient client, String domain, String version, String action,
        Map<String, String> params) throws Exception {
        Assert.notNull(client, "client can not be null!");
        Assert.hasText(domain, "domain can not be blank!");
        Assert.hasText(version, "version can not be blank!");
        Assert.hasText(action, "action can not be blank!");
        CommonRequest request = new CommonRequest();
        request.setSysDomain(domain);
        request.setSysVersion(version);
        request.setSysAction(action);
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                request.putQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        request.setSysProtocol(ProtocolType.HTTPS);
        CommonResponse response = client.getCommonResponse(request);
        logger.debug("==========================api info===============================");
        logger.debug(
            "[" + request.getSysDomain() + "][" + request.getSysVersion() + "][" + request.getSysAction() + "]");
        logger.debug(JsonUtils.toJsonString(request.getSysQueryParameters()));
        logger.debug("===========================output================================");
        logger.debug(JsonUtils.toJsonString(JSON.parse(response.getData())));
        return response.getData();
    }

    public static String commonInvoke(IAcsClient client, String domain, String version, String action)
        throws Exception {
        return commonInvoke(client, domain, version, action, null);
    }

    /**
     * 根据ak build client
     *
     * @param accessKeyID
     * @param accessKeySecret
     * @param region
     * @return
     */
    public static IAcsClient buildClient(String accessKeyID, String accessKeySecret, String region) {
        Assert.notNull(accessKeyID, "accessKeyID can not be null!");
        Assert.hasText(accessKeySecret, "accessKeySecret can not be blank!");
        Assert.hasText(region, "region can not be blank!");
        DefaultProfile profile = DefaultProfile.getProfile(region);
        IAcsClient client = new DefaultAcsClient(profile, new BasicCredentials(accessKeyID, accessKeySecret));
        return client;
    }

    public static IAcsClient buildClient_Hangzhou(String accessKeyID, String accessKeySecret) {
        return buildClient(accessKeyID, accessKeySecret, Region_Hangzhou);
    }

    /**
     * AssumeRole到指定账号
     *
     * @param accessKeyID 源账号AK
     * @param accessKeySecret
     * @param region
     * @param roleArn 示例:"acs:ram::145734573456334:role/resourcedirectoryaccountaccessrole",需要对应的role信任该账号
     * @return
     */
    public static IAcsClient buildSTSAssumeRoleClient(String accessKeyID, String accessKeySecret, String region,
        String roleArn) {
        DefaultProfile profile = DefaultProfile.getProfile(region);
        BasicCredentials credentials = new BasicCredentials(accessKeyID, accessKeySecret);
        STSAssumeRoleSessionCredentialsProvider provider = new STSAssumeRoleSessionCredentialsProvider(credentials,
            roleArn, profile);
        DefaultAcsClient client = new DefaultAcsClient(profile, provider);
        return client;
    }

    /**
     * 获取STS Token的client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param sessionToken
     * @param roleSessionDurationSeconds
     * @param region
     * @return
     */
    public static IAcsClient buildSTSTokenClient(String accessKeyId, String accessKeySecret, String sessionToken,
        long roleSessionDurationSeconds, String region) {
        DefaultProfile profile = DefaultProfile.getProfile(region);
        BasicSessionCredentials credentials = new BasicSessionCredentials(accessKeyId, accessKeySecret, sessionToken,
            roleSessionDurationSeconds);
        IAcsClient client = new DefaultAcsClient(profile, credentials);
        return client;
    }

    public static IAcsClient test(String accessKeyId, String accessKeySecret, String sessionToken,
        long roleSessionDurationSeconds, String region) {
        DefaultProfile profile = DefaultProfile.getProfile(region);
        BasicSessionCredentials credentials = new BasicSessionCredentials(accessKeyId, accessKeySecret, sessionToken,
            roleSessionDurationSeconds);
        IAcsClient client = new DefaultAcsClient(profile, credentials);
        return client;
    }

    // =================================================================================

    public static String getSigninToken(String accessKeyID, String accessKeySecret, String roleArn, String sessionName,
        String policy,
        boolean needTicketType)
        throws Exception {
        IAcsClient client = AliyunAPIUtils.buildClient_Hangzhou(accessKeyID, accessKeySecret);

        AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest();
        assumeRoleRequest.setRoleArn(roleArn);
        assumeRoleRequest.setRoleSessionName(sessionName);

        if (!policy.isBlank()) {
            assumeRoleRequest.setPolicy(policy);
        }

        AssumeRoleResponse assumeRoleResponse = client.getAcsResponse(assumeRoleRequest);
        assumeRoleResponse.getCredentials();

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("Action", "GetSigninToken");
        paramsMap.put("AccessKeyId", assumeRoleResponse.getCredentials().getAccessKeyId());
        paramsMap.put("AccessKeySecret", assumeRoleResponse.getCredentials().getAccessKeySecret());
        paramsMap.put("SecurityToken", assumeRoleResponse.getCredentials().getSecurityToken());
        if (needTicketType) {
            paramsMap.put("TicketType", "mini");
        }
        String result = HttpClientUtils.postUrlAndStringBody("https://signin.aliyun.com/federation", paramsMap);
        String signinToken = JsonUtils.getValueFormJsonString(result, "SigninToken", String.class);
        return signinToken;
    }

}
