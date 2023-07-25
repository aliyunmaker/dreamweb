package cc.landingzone.dreamcmp.common.utils;

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

import cc.landingzone.dreamcmp.common.CommonConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;


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

    public static void main1(String[] args) throws Exception {
        // testAssumeRoleWithOIDC_aliyun();
        testAssumeRoleWithOIDC_chengchaoOIDC();
        // testAssumeRole();
        // testECSList();
        // getSigninToken(CommonConstants.Aliyun_RAM_Admin_AccessKeyId,
        // CommonConstants.Aliyun_RAM_Admin_AccessKeySecret,
        // "acs:ram::1764263140474643:role/cmpadmin", Policy_ECS_FULL, false);
    }

    public static void main(String[] args) throws Exception {
        // testAssumeRoleWithOIDC_chengchaoOIDC();

        String accesskeyID = "STS.NSp9NVpNaejZP1e9EteYRq3pA";
        String accessKeySecret = "8j3rncBMuU6upMgta4SAbiLjSpby9gE9QGLGFgzMawsH";
        String securityToken =
                "CAISgAJ1q6Ft5B2yfSjIr5DFcvTinZFA0qixUhfU3UUhadZ+nvbbozz2IHhMe3hsAesXsvwzlG9W7vYelqJ4S5ZfQUHfccZr8szYf/wjl9KT1fau5Jko1beHewHKeTOZsebWZ+LmNqC/Ht6md1HDkAJq3LL+bk/Mdle5MJqP+/UFB5ZtKWveVzddA8pMLQZPsdITMWCrVcygKRn3mGHdfiEK00he8Tousv/mmJfEs0eB0Qymk7Qvyt6vcsT+Xa5FJ4xiVtq55utye5fa3TRYgxowr/8u0/0YoWyW4onBUwYPu0XaKZTd/thuKgR5a641EK5Nq/71kPB/u+DYh1kHGaaFkD4+GoABnfItuA7kVUsc2R/EKHjE2yqYd5wgGpBdrXaRCNDgz/XkiarCKm75Cw6Dr17X5K9v+l0nurVqAEHqnGSpOzOPijzNB3v4N3/u+0T5jGsZRQPzEDnhuxQcXMrszQq6PYAVF9Ty4O2UdBo1jChAy3bcrEixtOZW9MfUCwrMdOLqPBE=";

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("Action", "GetSigninToken");
        paramsMap.put("AccessKeyId", accesskeyID);
        paramsMap.put("AccessKeySecret", accessKeySecret);
        paramsMap.put("SecurityToken", securityToken);
        // paramsMap.put("TicketType", "mini");
        String result = HttpClientUtils.postUrlAndStringBody("https://signin.aliyun.com/federation", paramsMap);
        String signinToken = JsonUtils.getValueFormJsonString(result, "SigninToken", String.class);
        System.out.println(signinToken);
    }

    public static String getSigninToken(String accessKeyID, String accessKeySecret, String roleArn, String sessionName, String policy,
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

    /**
     * 这个API中的RegionId会被强制替换成client中的RegionId,代码在<br>
     * com.aliyuncs.RpcAcsRequest.signRequest(Signer,AlibabaCloudCredentials,FormatType, ProductDomain)这个方法里<br>
     * imutableMap = this.composer.refreshSignParameters(this.getSysQueryParameters(), signer,accessKeyId, format); <br>
     * imutableMap.put("RegionId", getSysRegionId());
     *
     * @throws Exception
     */
    public static void testECSList() throws Exception {
        IAcsClient client = AliyunAPIUtils.buildClient(CommonConstants.Aliyun_AccessKeyId,
                CommonConstants.Aliyun_AccessKeySecret,
                Region_Hangzhou);
        String action = "DescribeInstances";
        Map<String, String> params = new HashMap<>();
        // params.put("RegionId", "这个参数会被替换");
        // params.put("VpcId", "vpc-bp182kz55a6sl6tnl8my1");
        String result = AliyunAPIUtils.commonInvoke(client, "ecs.aliyuncs.com", "2014-05-26", action, params);
        System.out.println(result);
    }

    // 这里测试AssumeRole 叠加Policy.需要注意的是这里的AK只能是RAM的.不能是主账号的.
    public static String testAssumeRole() throws Exception {
        IAcsClient client = AliyunAPIUtils.buildClient_Hangzhou("LTAI5tAoHxkEyL8URYPi3EwW",
                "5clwhxzii0DuCmkudJaROdoPZvK1d7");
        String action = "AssumeRole";
        Map<String, String> params = new HashMap<>();
        params.put("RoleArn", "acs:ram::1933122015759413:role/testassumerole0315");
        params.put("RoleSessionName", "charlestest1");
        params.put("Policy",
                "{\"Statement\": [{\"Action\": [\"*\"],\"Effect\": \"Allow\",\"Resource\": [\"*\"]}],\"Version\":\"1\"}");
        return AliyunAPIUtils.commonInvoke(client, "sts.cn-hangzhou.aliyuncs.com", "2015-04-01", action, params);
    }

    public static String testAssumeRoleWithOIDC_aliyun() throws Exception {
        IAcsClient client = AliyunAPIUtils.buildClient_Hangzhou("noneed", "noneed");
        String action = "AssumeRoleWithOIDC";
        Map<String, String> params = new HashMap<>();
        params.put("OIDCProviderArn", "acs:ram::1933122015759413:oidc-provider/aliyunoidc");
        params.put("RoleArn", "acs:ram::1933122015759413:role/adminoidcaliyun");
        params.put("OIDCToken",
                "eyJraWQiOiJKQzl3eHpyaHFKMGd0YUNFdDJRTFVmZXZFVUl3bHRGaHVpNE8xYmg2N3RVIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOiI0OTQ5MDA5NjQ3Mzk3MzI2MzgxIiwic3ViIjoiZStOSXByS015Ulk4OHFmKytTeCttdz09IiwiaXNzIjoiaHR0cHM6XC9cL29hdXRoLmFsaXl1bi5jb20iLCJleHAiOjE2NDc1MDczNTgsImlhdCI6MTY0NzUwMzc1OH0.aXdDxzjO9hODr936J1QSZ1FtBYPbS-rUhs_222huiAC8Ikr7GEPpyBx9piWVfC2LXtN5yCMhcfwe5HNNnn44ez6nlFGC9JaNZr3_Me6GsaE10NLxJ450DUCJAofDXex_8VgtkR0Ipz4ebYLkD_WI36lk4FfX-93_SFqIk1itMTcLAmIc1ceXWw6C1EX3HwG64hEgqotkKMB4LxwqT5nD8LhPFeaku6JT97Rtb9GFDbLt3aRfxZKYAbmvLZEc-i7PxIv4EaLQkDEadE1_kv8_MqVxIDpu92zSdGMWNH5_kEKNzoBC7znzOaF3iImrQH8_TkgRV2f0bo2fQ7aj7hxNXA");
        params.put("RoleSessionName", "charlestest");
        return AliyunAPIUtils.commonInvoke(client, "sts.cn-hangzhou.aliyuncs.com", "2015-04-01", action, params);
    }

    public static String testAssumeRoleWithOIDC_authing() throws Exception {
        IAcsClient client = AliyunAPIUtils.buildClient_Hangzhou("noneed", "noneed");
        String action = "AssumeRoleWithOIDC";
        Map<String, String> params = new HashMap<>();
        params.put("OIDCProviderArn", "acs:ram::1933122015759413:oidc-provider/authingoidc");
        params.put("RoleArn", "acs:ram::1933122015759413:role/adminauthingoidc");
        params.put("OIDCToken",
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkZGeW9XRm9VdHFwY09feDhJX21GalBHUk8xOFRCczFyUGJFS0d1ekhSRUUifQ.eyJwaG9uZV9udW1iZXIiOm51bGwsInBob25lX251bWJlcl92ZXJpZmllZCI6ZmFsc2UsImVtYWlsIjpudWxsLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInN1YiI6IjYyMzJhZGVkOTE0MDJlN2RmNzRmYzA3NCIsImJpcnRoZGF0ZSI6bnVsbCwiZmFtaWx5X25hbWUiOm51bGwsImdlbmRlciI6IlUiLCJnaXZlbl9uYW1lIjpudWxsLCJsb2NhbGUiOm51bGwsIm1pZGRsZV9uYW1lIjpudWxsLCJuYW1lIjpudWxsLCJuaWNrbmFtZSI6bnVsbCwicGljdHVyZSI6Imh0dHBzOi8vZmlsZXMuYXV0aGluZy5jby91c2VyLWNvbnRlbnRzL2F2YXRhci9kZWZhdWx0R2VuZXJhdGUtZTBkYTIyZDEtN2NhYS00MWFiLTlhM2UtYTczYjQ4OTBlNWU2LnN2ZyIsInByZWZlcnJlZF91c2VybmFtZSI6bnVsbCwicHJvZmlsZSI6bnVsbCwidXBkYXRlZF9hdCI6IjIwMjItMDMtMTdUMDc6MTY6MDkuMzkwWiIsIndlYnNpdGUiOm51bGwsInpvbmVpbmZvIjpudWxsLCJub25jZSI6IlNlZ0JOOElDV04iLCJhdF9oYXNoIjoiNk1US25hY1BlWU54Q21fZkFvNHg2ZyIsImF1ZCI6IjYyMzJhZGVkYjJkMTlkOWY3ZDEzNDRjNiIsImV4cCI6MTY0ODcxNDQwNSwiaWF0IjoxNjQ3NTA0ODA1LCJpc3MiOiJodHRwczovL2RoYW1tbG9qZW9saS1kZW1vLmF1dGhpbmcuY24vb2lkYyJ9.NLd5ag3RpE3Fu4KF5Nlpyf8nGk18jhbsHa8wqfxGkzpEnOUib0WC45MkZZMQljIXZU908MKMYrnoldErcwoRNyhwIgcQyPnHHd4k5WFA83AbU5BkRkae8keRwBLU9c0ybjIcyh4Fcl3UnVDDo0w8R0uAIfVWeBUJKxbX9m7_aoVP0nloV2WZwfqvYoyqG03N9JxsO28v4lYfxIoXpmqZz0ArCeSXWkQT5z1Dale6_9W6qGMO6XXyxU2L0YVGnYWPOuGRbyaFe4SH_ofntLxjPfutTqiOFtDSLsRcxxcMf09efrsecelrcwa82_y-u2cBe3cv4JHdEkzOUQAuQbysMQ");
        params.put("RoleSessionName", "charlestest");
        return AliyunAPIUtils.commonInvoke(client, "sts.cn-hangzhou.aliyuncs.com", "2015-04-01", action, params);
    }

    public static String testAssumeRoleWithOIDC_chengchaoOIDC() throws Exception {

        String idToken = HttpClientUtils.getDataAsStringFromUrl("https://chengchao.name/springrun/oauth2/idtoken");
        // 用OIDC换取STS Token是不需要AK的
        IAcsClient client = AliyunAPIUtils.buildClient_Hangzhou("noneed", "noneed");
        String action = "AssumeRoleWithOIDC";
        Map<String, String> params = new HashMap<>();
        params.put("OIDCProviderArn", "acs:ram::1933122015759413:oidc-provider/chengchaoname");
        params.put("RoleArn", "acs:ram::1933122015759413:role/adminchengchaonameoidc");
        params.put("OIDCToken", idToken);
        params.put("RoleSessionName", "charlestest");
        return AliyunAPIUtils.commonInvoke(client, "sts.cn-hangzhou.aliyuncs.com", "2015-04-01", action, params);
    }

}
