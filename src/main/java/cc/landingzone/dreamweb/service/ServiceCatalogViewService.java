package cc.landingzone.dreamweb.service;

import cc.landingzone.dreamweb.common.EndpointEnum;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.enums.SSOSpEnum;
import cc.landingzone.dreamweb.sso.SSOConstants;
import cc.landingzone.dreamweb.sso.SamlGenerator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cc.landingzone.dreamweb.utils.JsonUtils;
import com.aliyun.servicecatalog20210901.Client;
import com.aliyun.teaopenapi.models.Config;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * 获取服务目录免密登陆URL以及开启产品操作
 *
 * @author: laodou
 * @createDate: 2022/6/21
 *
 */
@Service
public class ServiceCatalogViewService {

    private static Logger logger = LoggerFactory.getLogger(ServiceCatalogViewService.class);

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 使用特定角色获取免登录链接
     *
     * @param servicecatalogProductId  产品ID
     * @param provisionedProductName 实例名称
     * @param region       地区
     * @param user         用户
     * @param userRole     角色
     * @return 免登录链接
     * @throws Exception
     */
    public String getNonLoginPreUrl(String servicecatalogProductId, String provisionedProductName, String region, User user,
                                    UserRole userRole, String servicecatalogProductVersionId, String servicecatalogPortfolioId, String regionSelect)
        throws Exception {
        String signInUrl = "";
        String stsEndpoint = EndpointEnum.STS.getEndpoint();
        String signinEndpoint = EndpointEnum.SIGN_IN.getEndpoint();

        // 得到roleArn和idpArn，生成Saml Assertion
        String[] roleValue = userRole.getRoleValue().split(",");
        String roleArn = roleValue[0];
        String samlProviderArn = roleValue[1];
        String samlAssertion = getSamlAssertion(user, userRole);

        // 访问令牌服务获取AK、SK和SecurityToken
        CommonResponse commonResponse = requestAccessKeyAndSecurityToken(region, roleArn, samlProviderArn,
            samlAssertion, stsEndpoint, servicecatalogProductId);
        Assert.notNull(commonResponse, "assumeRole获取失败");

        // 通过临时AK、SK以及SecurityToken获取SignInToken
        String signInToken = requestSignInToken(commonResponse, signinEndpoint);
        Assert.notNull(signInToken, "signInToken获取失败");

        // 通过登录token生成日志服务web访问链接进行跳转
        signInUrl = generateSignInUrl(signInToken, servicecatalogProductId, provisionedProductName, signinEndpoint, servicecatalogProductVersionId, servicecatalogPortfolioId, regionSelect);
        Assert.notNull(signInUrl, "signInUrl生成失败");

        return signInUrl;
    }
/**
     * 获取产品实例之前创建终端
     *
     * @param: 地域、用户、角色
     * @return 终端
     * @throws Exception
     */
    public Client createClient(String region, User user, UserRole userRole, String servicecatalogProductId) throws Exception {
        String stsEndpoint = EndpointEnum.STS.getEndpoint();

        // 得到roleArn和idpArn，生成Saml Assertion
        String[] roleValue = userRole.getRoleValue().split(",");
        String roleArn = roleValue[0];
        String samlProviderArn = roleValue[1];
        String samlAssertion = getSamlAssertion(user, userRole);

        // 访问令牌服务获取临时AK和Token
        CommonResponse commonResponse = requestAccessKeyAndSecurityToken(region, roleArn, samlProviderArn,
                samlAssertion, stsEndpoint, servicecatalogProductId);
        Assert.notNull(commonResponse, "assumeRole获取失败");

        JSONObject assumeRole = JSONObject.parseObject(commonResponse.getData());
        JSONObject credentials = assumeRole.getJSONObject("Credentials");

        String SecurityToken = credentials.getString("SecurityToken");
        String Access_Key_Id = credentials.getString("AccessKeyId");
        String Access_Key_secret = credentials.getString("AccessKeySecret");

        Config config = new Config();
        config.setAccessKeyId(Access_Key_Id);
        config.setAccessKeySecret(Access_Key_secret);
        config.setRegionId(region);
        config.setSecurityToken(SecurityToken);
        Client client = new Client(config);
        return client;
    }

    /**
     * 生成Saml Assertion
     *
     * @param user     用户
     * @param userRole 角色
     * @return Saml Assertion
     * @throws Exception
     */
    private String getSamlAssertion(User user, UserRole userRole) throws Exception {
        List<UserRole> roleList = userRoleService.getRoleListByUserId(user.getId(), SSOSpEnum.aliyun);
        io.jsonwebtoken.lang.Assert.notEmpty(roleList, "roleList can not be empty!");

        SSOSpEnum ssoSp = SSOSpEnum.aliyun;

        String nameID = user.getLoginName();
        String identifier = SSOConstants.getSSOSpIdentifier(ssoSp);
        String replyUrl = SSOConstants.getSSOSpReplyUrl(ssoSp);

        HashMap<String, List<String>> attributes = new HashMap<>();
        // 只有role sso 才需要这些参数
        Set<String> roleSet = new HashSet<>();
        roleSet.add(userRole.getRoleValue());

        List<String> roleStringList = new ArrayList<>(roleSet);
        attributes.put(SSOConstants.getSSOSpAttributeKeyRole(ssoSp), roleStringList);
        List<String> sessionNameList = new ArrayList<>();
        sessionNameList.add(nameID);
        attributes.put(SSOConstants.getSSOSpAttributeKeyRoleSessionName(ssoSp), sessionNameList);

        String samlAssertion = SamlGenerator.generateResponse(identifier, replyUrl, nameID, attributes);

        return samlAssertion;
    }

    /**
     * 访问令牌服务获取AK、SK和SecurityToken(policy自定义权限)
     *
     * @return 临时AK和Token
     * @throws ClientException
     */
    private CommonResponse requestAccessKeyAndSecurityToken( String region, String roleArn,
                                                            String samlProviderArn, String samlAssertion,
                                                            String stsEndpoint, String servicecatalogProductId)
        throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile(region, "", "");

        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysProtocol(ProtocolType.HTTPS);
        request.setSysDomain(stsEndpoint);
        request.setSysVersion("2015-04-01");
        request.setSysAction("AssumeRoleWithSAML");

        request.putQueryParameter("RoleArn", roleArn);
        request.putQueryParameter("SAMLProviderArn", samlProviderArn);
        request.putQueryParameter("SAMLAssertion", samlAssertion);


        JSONObject policy = new JSONObject();

        JSONObject Statement1 = new JSONObject();
        JSONObject Statement2 = new JSONObject();
        JSONObject Statement3 = new JSONObject();
        JSONObject Statement4 = new JSONObject();
        String[] Action1 = {
                "servicecatalog:GetProductAsEndUser",
                "servicecatalog:ListLaunchOptions",
                "servicecatalog:ListProductVersions",
                "servicecatalog:GetTemplate",
                "servicecatalog:GetProvisionedProductPlan",
                "servicecatalog:ExecuteProvisionedProductPlan",
                "servicecatalog:CreateProvisionedProductPlan",
                "servicecatalog:GetProvisionedProduct",
                "servicecatalog:GetTask"};

        List<String> Resource = new ArrayList<>();
        Resource.add("acs:servicecatalog:cn-hangzhou:1466115886172051:product/" + servicecatalogProductId);
        Resource.add("acs:servicecatalog:cn-hangzhou:1466115886172051:provisionedproduct/*");

        String[] Action2 = {"ros:GetTemplate",
                "ros:ValidateTemplate",
                "ros:CreateStack",
                "ros:ContinueCreateStack",
                "ros:GetStack",
                "ros:UpdateStack",
                "ros:DeleteStack",
                "ros:ListStacks",
                "ros:ListStackEvents",
                "ros:ListStackResources",
                "ros:ListChangeSets"};
        String[] Resource2 = {"*"};
        String[] Action3 = {
                "ram:ListUsers",
                "ros:ValidateTemplate",
                "ram:ListRoles"};
        String[] Action4 = {"servicecatalog:GetProvisionedProduct", "servicecatalog:GetTask"};
        JSONObject ser = new JSONObject();
        ser.put("servicecatalog:UserLevel", "self");
        JSONObject StringEquals = new JSONObject();
        StringEquals.put("StringEquals", ser);

        Statement1.put("Effect", "Allow");
        Statement1.put("Action", Action1);
        Statement1.put("Resource", Resource);


        Statement2.put("Effect", "Allow");
        Statement2.put("Action", Action2);
        Statement2.put("Resource", Resource2);

        Statement3.put("Effect", "Allow");
        Statement3.put("Action", Action3);
        Statement3.put("Resource", Resource2);

        Statement4.put("Effect", "Allow");
        Statement4.put("Action", Action4);
        Statement4.put("Resource", Resource2);
        Statement4.put("Condition", StringEquals);

        List<JSONObject> statement = new ArrayList<>();
        statement.add(Statement1);
        statement.add(Statement3);
        statement.add(Statement4);

        policy.put("Version", "1");
        policy.put("Statement",statement);
        request.putQueryParameter("Policy", policy.toJSONString());

        CommonResponse response = client.getCommonResponse(request);
        return response;
    }

    /**
     * 通过临时AK、SK以及SecurityToken获取SignInToken
     *
     * @param commonResponse 临时AK & Token
     * @return 登录Token
     * @throws IOException
     */
    private String requestSignInToken(CommonResponse commonResponse, String endpoint) throws IOException {
        JSONObject assumeRole = JSONObject.parseObject(commonResponse.getData());
        JSONObject credentials = assumeRole.getJSONObject("Credentials");

        String signInTokenUrl = endpoint + String.format(
            "/federation?Action=GetSigninToken"
                + "&AccessKeyId=%s"
                + "&AccessKeySecret=%s"
                + "&SecurityToken=%s&TicketType=mini",
            URLEncoder.encode(credentials.getString("AccessKeyId"), "utf-8"),
            URLEncoder.encode(credentials.getString("AccessKeySecret"), "utf-8"),
            URLEncoder.encode(credentials.getString("SecurityToken"), "utf-8")
        );

        HttpGet signInGet = new HttpGet(signInTokenUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse = httpClient.execute(signInGet);
        String signInToken = "";
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            String signInRes = EntityUtils.toString(httpResponse.getEntity());
            logger.info("received signInRes: {}", signInRes);
            signInToken = JSON.parseObject(signInRes).getString("SigninToken");

            if (signInToken == null) {
                logger.error("Invalid response message, contains no SigninToken: {}", signInRes);
            }
        } else {
            logger.error("Failed to retrieve signInToken");
        }

        return signInToken;
    }

    /**
     * 通过登录token生成服务目录web访问链接进行跳转
     *
     * @param signInToken 登录token
     * @param productId   产品id
     * @return 免登录Url
     * @throws UnsupportedEncodingException
     */
    private String generateSignInUrl(String signInToken, String servicecatalogProductId, String provisionedProductName, String endpoint, String servicecatalogProductVersionId, String servicecatalogPortfolioId, String regionSelect)
            throws UnsupportedEncodingException {

        Map<String, String> planButtonText = new HashMap<>();
        planButtonText.put("zh-cn", "提交申请");

        Map<String, Object> style = new HashMap<>();
        style.put("displayMode", "cmp");
        style.put("planButtonText", planButtonText);

        Map<String, Object> controlParameters = new HashMap<>();
        controlParameters.put("style", style);
        controlParameters.put("provisionedProductName", provisionedProductName);
        controlParameters.put("portfolioId", servicecatalogPortfolioId);
        controlParameters.put("productVersionId", servicecatalogProductVersionId);
        if(regionSelect.equals("上海")) {
            controlParameters.put("stackRegionId", "cn-shanghai");
        } else if(regionSelect.equals("杭州")) {
            controlParameters.put("stackRegionId", "cn-hangzhou");
        }
        String controlString = JsonUtils.toJsonString(controlParameters);
        String base64EncodedControlString = Base64.getUrlEncoder().encodeToString(controlString.getBytes(StandardCharsets.UTF_8));

        String preUrl = String.format("https://servicecatalog4service.console.aliyun.com/products"
                + "/launch?productId=%s&controlString=%s",
            URLEncoder.encode(servicecatalogProductId, "utf-8"),
            URLEncoder.encode(base64EncodedControlString, "utf-8"));

        String signInUrl = endpoint + String.format(
                "/federation?Action=Login"
                        + "&LoginUrl=%s"
                        + "&Destination=%s"
                        + "&SigninToken=%s",
                URLEncoder.encode("https://www.aliyun.com", "utf-8"),
                URLEncoder.encode(preUrl, "utf-8"),
                URLEncoder.encode(signInToken, "utf-8"));
        return signInUrl;
    }

}
