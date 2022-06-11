package cc.landingzone.dreamweb.service;

import java.io.UnsupportedEncodingException;
import java.util.*;

import java.io.IOException;

import cc.landingzone.dreamweb.utils.JsonUtils;
import com.aliyun.servicecatalog20210901.Client;
import com.aliyun.servicecatalog20210901.models.*;
import com.aliyun.teaopenapi.models.Config;
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

    public List<String> listProductsAsEndUser1(String region, User user, UserRole userRole) throws Exception {
        List<String> lists = new ArrayList<>();
        Client client = createClient(region, user, userRole);
        
//        List<ListPortfoliosResponseBody.ListPortfoliosResponseBodyPortfolioDetails> portfolioDetails = listPortfolios(client);
//        List<ListProductsAsAdminResponseBody.ListProductsAsAdminResponseBodyProductDetails> productDetails = listProductsAsAdmin(client);
        List<ListProductsAsEndUserResponseBody.ListProductsAsEndUserResponseBodyProductSummaries> productSummaries = listProductsAsEndUser(client);
//        System.out.println(JsonUtils.toJsonString(portfolioDetails) + "\n");
//        System.out.println(JsonUtils.toJsonString(productDetails) + "\n");
        for(int i = 0; i < productSummaries.size(); i ++){
//            System.out.println(productSummaries.get(i).getProductId());
            lists.add(productSummaries.get(i).getProductId());
        }
//        System.out.println(JsonUtils.toJsonString(productSummaries));
        return lists;
    }

    public Client createClient(String region, User user, UserRole userRole) throws Exception {
        String stsEndpoint = EndpointEnum.STS.getEndpoint();

        // 得到roleArn和idpArn，生成Saml Assertion
        String[] roleValue = userRole.getRoleValue().split(",");
        String roleArn = roleValue[0];
        String samlProviderArn = roleValue[1];
        String samlAssertion = getSamlAssertion(user, userRole);

        // 访问令牌服务获取临时AK和Token
        CommonResponse commonResponse = requestAccessKeyAndSecurityToken(region, roleArn, samlProviderArn,
                samlAssertion, stsEndpoint);
        Assert.notNull(commonResponse, "assumeRole获取失败");

        JSONObject assumeRole = JSONObject.parseObject(commonResponse.getData());
        JSONObject credentials = assumeRole.getJSONObject("Credentials");

        String SecurityToken = credentials.getString("SecurityToken");
//        System.out.println(SecurityToken);
        String Access_Key_Id = credentials.getString("AccessKeyId");
//        System.out.println(Access_Key_Id);
        String Access_Key_secret = credentials.getString("AccessKeySecret");
//        System.out.println(Access_Key_secret);

        Config config = new Config();
        config.setAccessKeyId(Access_Key_Id);
        config.setAccessKeySecret(Access_Key_secret);
        config.setRegionId(region);
        config.setSecurityToken(SecurityToken);
        Client client = new Client(config);
        return client;
    }


    public static List<ListPortfoliosResponseBody.ListPortfoliosResponseBodyPortfolioDetails> listPortfolios(Client client) throws Exception {
        List<ListPortfoliosRequest.ListPortfoliosRequestFilters> filters = new ArrayList<ListPortfoliosRequest.ListPortfoliosRequestFilters>() {{
            ListPortfoliosRequest.ListPortfoliosRequestFilters filter = new ListPortfoliosRequest.ListPortfoliosRequestFilters();
            filter.setKey("FullTextSearch");
            filter.setValue("API");
            add(filter);
        }};

        int pageNumber = 1;
        int pageSize = 2;

        ListPortfoliosRequest request = new ListPortfoliosRequest();
        request.setFilters(filters);
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        request.setSortBy("CreateTime");
        request.setSortOrder("Desc");

        ListPortfoliosResponse response = client.listPortfolios(request);
        ListPortfoliosResponseBody responseBody = response.getBody();
        int total = responseBody.getTotalCount();

        List<ListPortfoliosResponseBody.ListPortfoliosResponseBodyPortfolioDetails> portfolioDetails = new ArrayList<>();
        portfolioDetails.addAll(responseBody.getPortfolioDetails());
        while (pageNumber * pageSize < total) {
            pageNumber++;
            request.setPageNumber(pageNumber);
            response = client.listPortfolios(request);
            responseBody = response.getBody();
            portfolioDetails.addAll(responseBody.getPortfolioDetails());
        }

        return portfolioDetails;
    }

    public static List<ListProductsAsAdminResponseBody.ListProductsAsAdminResponseBodyProductDetails> listProductsAsAdmin(Client client) throws Exception {
        int pageNumber = 1;
        int pageSize = 2;

        ListProductsAsAdminRequest request = new ListProductsAsAdminRequest();
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        request.setSortBy("CreateTime");
        request.setSortOrder("Desc");

        ListProductsAsAdminResponse response = client.listProductsAsAdmin(request);
        ListProductsAsAdminResponseBody responseBody = response.getBody();
        int total = responseBody.getTotalCount();

        List<ListProductsAsAdminResponseBody.ListProductsAsAdminResponseBodyProductDetails> productDetails = new ArrayList<>();
        productDetails.addAll(responseBody.getProductDetails());
        while (pageNumber * pageSize < total) {
            pageNumber++;
            request.setPageNumber(pageNumber);
            response = client.listProductsAsAdmin(request);
            responseBody = response.getBody();
            productDetails.addAll(responseBody.getProductDetails());
        }

        return productDetails;
    }

    public static List<ListProductsAsEndUserResponseBody.ListProductsAsEndUserResponseBodyProductSummaries> listProductsAsEndUser(Client client) throws Exception {
        int pageNumber = 1;
        int pageSize = 2;

        ListProductsAsEndUserRequest request = new ListProductsAsEndUserRequest();
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        request.setSortBy("CreateTime");
        request.setSortOrder("Desc");

        ListProductsAsEndUserResponse response = client.listProductsAsEndUser(request);
        ListProductsAsEndUserResponseBody responseBody = response.getBody();
        int total = responseBody.getTotalCount();

        List<ListProductsAsEndUserResponseBody.ListProductsAsEndUserResponseBodyProductSummaries> productSummaries = new ArrayList<>();
        productSummaries.addAll(responseBody.getProductSummaries());
        while (pageNumber * pageSize < total) {
            pageNumber++;
            request.setPageNumber(pageNumber);
            response = client.listProductsAsEndUser(request);
            responseBody = response.getBody();
            productSummaries.addAll(responseBody.getProductSummaries());
        }

        return productSummaries;
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
