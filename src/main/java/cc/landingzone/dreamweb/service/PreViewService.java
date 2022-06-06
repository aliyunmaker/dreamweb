package cc.landingzone.dreamweb.service;

import java.io.UnsupportedEncodingException;
import java.util.*;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cc.landingzone.dreamweb.common.EndpointEnum;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.enums.SSOSpEnum;
import cc.landingzone.dreamweb.sso.SamlGenerator;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import java.net.URLEncoder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.aliyuncs.CommonResponse;
import cc.landingzone.dreamweb.sso.SSOConstants;
import org.springframework.util.Assert;



@Service
public class PreViewService {

    private static Logger logger = LoggerFactory.getLogger(PreViewService.class);

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 使用特定角色获取免登录链接
     *
     * @param productId  项目ID
     * @param exampleName 实例名称
     * @param region       地区
     * @param user         用户
     * @param userRole     角色
     * @return 免登录链接
     * @throws Exception
     */
    public String getNonLoginPreUrl(String productId, String exampleName, String region, User user,
                                    UserRole userRole)
        throws Exception {
        String signInUrl = "";
        String stsEndpoint = EndpointEnum.STS.getEndpoint();
        String signinEndpoint = EndpointEnum.SIGN_IN.getEndpoint();

        // 得到roleArn和idpArn，生成Saml Assertion
        String[] roleValue = userRole.getRoleValue().split(",");
        String roleArn = roleValue[0];
        String samlProviderArn = roleValue[1];
        String samlAssertion = getSamlAssertion(user, userRole);

        // 访问令牌服务获取临时AK和Token
        CommonResponse commonResponse = requestAccessKeyAndSecurityToken(region, roleArn, samlProviderArn,
            samlAssertion, stsEndpoint);
        Assert.notNull(commonResponse, "assumeRole获取失败");

        // 通过临时AK & Token获取登录Token
        String signInToken = requestSignInToken(commonResponse, signinEndpoint);
        Assert.notNull(signInToken, "signInToken获取失败");

        // 通过登录token生成日志服务web访问链接进行跳转
        signInUrl = generateSignInUrl(signInToken, productId, exampleName, signinEndpoint);
        Assert.notNull(signInUrl, "signInUrl生成失败");

        return signInUrl;
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
     * 访问令牌服务获取临时AK和Token
     *
     * @return 临时AK和Token
     * @throws ClientException
     */
    private CommonResponse requestAccessKeyAndSecurityToken(String region, String roleArn, String samlProviderArn,
                                                            String samlAssertion, String stsEndpoint)
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
        CommonResponse response = client.getCommonResponse(request);
        return response;
    }

    /**
     * 通过临时AK & Token获取登录Token
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
    private String generateSignInUrl(String signInToken, String productId, String exampleName, String endpoint)
            throws UnsupportedEncodingException {
        String preUrl = String.format("https://pre-servicecatalog4service.console.aliyun.com/products"
                        + "/launch?productId=%s&provisionedProductName=%s&hideSidebar=true",
                URLEncoder.encode(productId, "utf-8"),
                URLEncoder.encode(exampleName, "utf-8"));
//        &provisionedProductName=sugar123&hideSidebar=true
//        String preUrl = "https://pre-servicecatalog4service.console.aliyun.com/products/launch?productId=prod-bp1qbazd242511&provisionedProductName=sugar123&hideSidebar=true";
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
