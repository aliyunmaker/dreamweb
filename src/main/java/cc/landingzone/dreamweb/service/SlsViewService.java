package cc.landingzone.dreamweb.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cc.landingzone.dreamweb.common.EndpointConstants;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.enums.SSOSpEnum;
import cc.landingzone.dreamweb.sso.SSOConstants;
import cc.landingzone.dreamweb.sso.SamlGenerator;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Project;
import com.aliyun.openservices.log.request.ListLogStoresRequest;
import com.aliyun.openservices.log.request.ListProjectRequest;
import com.aliyun.openservices.log.response.ListLogStoresResponse;
import com.aliyun.openservices.log.response.ListProjectResponse;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import cc.landingzone.dreamweb.model.Page;

@Service
public class SlsViewService {

    private static Logger logger = LoggerFactory.getLogger(SlsViewService.class);

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    UserService userService;

    // 5分钟刷新一次
    private LoadingCache<Integer, List<String>> cache = CacheBuilder.newBuilder()
        .maximumSize(100)
        .refreshAfterWrite(1, TimeUnit.MINUTES)
        .build(new CacheLoader<Integer, List<String>>() {
                   @Override
                   public List<String> load(Integer roleId) {
                       UserRole userRole = userRoleService.getUserRoleById(roleId);
                       // 获取当前用户信息，用于saml Assertion的生成
                       String userName = SecurityContextHolder.getContext().getAuthentication().getName();
                       User user = userService.getUserByLoginName(userName);
                       String region = systemConfigService.getStringValue("region");
                       Boolean useVpc = systemConfigService.getBoolValue("useVpc");
                       // 分页，limit最大是500
                       Page page = new Page(0, 500);

                       List<String> projectList = new ArrayList<>();
                       try {
                           projectList = listProjectsInfo(page, region, user, userRole, useVpc);
                       } catch (Exception e) {
                           logger.error(e.getMessage());
                       }
                       return projectList;
                   }
               }
        );

    /**
     * 获取该角色能够访问的所有Project，如何定义可以访问Project：可以列出Project下的所有Logstore即表示可以访问Project
     *
     * @param page     分页信息
     * @param region   地区
     * @param user     用户
     * @param userRole 角色
     * @return Project名称列表
     * @throws Exception
     */
    public List<String> listProjectsInfo(Page page, String region, User user, UserRole userRole, Boolean useVpc)
        throws Exception {
        String slsEndpoint = EndpointConstants.getSlsEndpoint(region, useVpc);
        String stsEndpoint = EndpointConstants.getStsEndpoint(region, useVpc);

        // 得到roleArn和idpArn，生成Saml Assertion
        String[] roleValue = userRole.getRoleValue().split(",");
        String roleArn = roleValue[0];
        String samlProviderArn = roleValue[1];
        String samlAssertion = getSamlAssertion(user, userRole);

        // 根据角色Arn和idpArn 获取AK，SK和SecurityToken
        CommonResponse commonResponse = requestAccessKeyAndSecurityToken(region, roleArn, samlProviderArn,
            samlAssertion, stsEndpoint);
        JSONObject assumeRole = JSONObject.parseObject(commonResponse.getData());
        JSONObject credentials = assumeRole.getJSONObject("Credentials");

        Client slsClient = new Client(slsEndpoint, credentials.getString("AccessKeyId"),
            credentials.getString("AccessKeySecret"));
        slsClient.setSecurityToken(credentials.getString("SecurityToken"));

        ListProjectRequest request = new ListProjectRequest("", page.getStart(), page.getLimit());
        ListProjectResponse response = slsClient.ListProject(request);

        // 查询出所有Project，使用ListLogstores方法测试role是否有访问Project的权限
        List<String> projectList = new ArrayList<>();
        for (Project project : response.getProjects()) {
            try {
                ListLogStoresRequest logStoresRequest = new ListLogStoresRequest(project.getProjectName(),
                    page.getStart(), page.getLimit(),
                    "");
                slsClient.ListLogStores(logStoresRequest);
                projectList.add(project.getProjectName());
            } catch (Exception e) {
                // 抛出异常则表示role无权访问该project
                logger.info(e.getMessage());
            }
        }

        return projectList;
    }

    /**
     * 使用角色获取特定Project下的所有Logstore
     *
     * @param projectName Project名称
     * @param page        分页
     * @param region      地区
     * @param user        用户
     * @param userRole    角色
     * @return Logstore列表
     * @throws Exception
     */
    public List<String> listLogstoresInfo(String projectName, Page page, String region, User user, UserRole userRole,
                                          Boolean useVpc)
        throws Exception {
        String slsEndpoint = EndpointConstants.getSlsEndpoint(region, useVpc);
        String stsEndpoint = EndpointConstants.getStsEndpoint(region, useVpc);

        // 得到roleArn和idpArn，生成Saml Assertion
        String[] roleValue = userRole.getRoleValue().split(",");
        String roleArn = roleValue[0];
        String samlProviderArn = roleValue[1];
        String samlAssertion = getSamlAssertion(user, userRole);

        // 根据角色Arn和idpArn 获取AK，SK和SecurityToken
        CommonResponse commonResponse = requestAccessKeyAndSecurityToken(region, roleArn, samlProviderArn,
            samlAssertion, stsEndpoint);
        JSONObject assumeRole = JSONObject.parseObject(commonResponse.getData());
        JSONObject credentials = assumeRole.getJSONObject("Credentials");

        Client slsClient = new Client(slsEndpoint, credentials.getString("AccessKeyId"),
            credentials.getString("AccessKeySecret"));
        slsClient.setSecurityToken(credentials.getString("SecurityToken"));

        ListLogStoresRequest logStoresRequest = new ListLogStoresRequest(projectName, page.getStart(), page.getLimit(),
            "");
        ListLogStoresResponse logStoresResponse = slsClient.ListLogStores(logStoresRequest);
        List<String> logstoreList = logStoresResponse.GetLogStores();

        return logstoreList;
    }

    /**
     * 使用特定角色获取免登录链接
     *
     * @param projectName  项目名称
     * @param logstoreName 日志库名称
     * @param region       地区
     * @param user         用户
     * @param userRole     角色
     * @return 免登录链接
     * @throws Exception
     */
    public String getNonLoginSlsUrl(String projectName, String logstoreName, String region, User user,
                                    UserRole userRole, Boolean useVpc)
        throws Exception {
        String signInUrl = "";
        String stsEndpoint = EndpointConstants.getStsEndpoint(region, useVpc);
        String signinEndpoint = EndpointConstants.getSignInEndpoint(region, useVpc);

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
        signInUrl = generateSignInUrl(signInToken, projectName, logstoreName, signinEndpoint);
        Assert.notNull(signInUrl, "signInUrl生成失败");

        return signInUrl;
    }

    /**
     * 从缓存中获取当前用户允许访问的所有Project
     *
     * @param user 用户信息
     * @return
     * @throws Exception
     */
    public List<String> getAllProjectsUnderUserFromCache(User user) throws Exception {
        List<UserRole> userRoleList = userRoleService.getRoleListByUserId(user.getId());
        // 确保project不重复
        Set<String> projectList = new HashSet<>();
        for (UserRole userRole : userRoleList) {
            projectList.addAll(cache.get(userRole.getId()));
        }

        return new ArrayList<>(projectList);
    }

    /**
     * 从缓存中获取当前用户特定角色下允许访问的Project
     *
     * @param roleId 角色信息
     * @return
     * @throws Exception
     */
    public List<String> getProjectsWithRoleIdFromCache(Integer roleId) throws Exception {
        return cache.get(roleId);
    }

    /**
     * 查询使用哪个角色访问Project，直接返回能够访问Project的第一个角色
     *
     * @param targetProject 目标Project
     * @return 使用的角色
     */
    public UserRole getAssumeRoleByProject(String targetProject) {
        ConcurrentMap<Integer, List<String>> concurrentMap = cache.asMap();
        for (Integer key : concurrentMap.keySet()) {
            for (String projectName : concurrentMap.get(key)) {
                if (projectName.equals(targetProject)) {
                    UserRole userRole = userRoleService.getUserRoleById(key);
                    return userRole;
                }
            }
        }

        return null;
    }

    /**
     * 刷新缓存中所有数据
     *
     * @throws Exception
     */
    public void refreshCache() throws Exception {
        logger.info("begin refresh");
        Set<Integer> keys = cache.asMap().keySet();
        for (Integer key : keys) {
            cache.refresh(key);
        }
        logger.info("finish refresh");
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
     * 通过登录token生成日志服务web访问链接进行跳转
     *
     * @param signInToken 登录token
     * @param project     项目名称
     * @param logstore    日志库名称
     * @return 免登录Url
     * @throws UnsupportedEncodingException
     */
    private String generateSignInUrl(String signInToken, String project, String logstore, String endpoint)
        throws UnsupportedEncodingException {
        String slsUrl = String.format("https://sls4service.console.aliyun.com/next"
                + "/project/%s"
                + "/logsearch/%s?hiddenBack=true&hiddenChangeProject=true&hiddenOverview=true&hideTopbar=true",
            URLEncoder.encode(project, "utf-8"),
            URLEncoder.encode(logstore, "utf-8"));

        String signInUrl = endpoint + String.format(
            "/federation?Action=Login"
                + "&LoginUrl=%s"
                + "&Destination=%s"
                + "&SigninToken=%s",
            URLEncoder.encode("https://www.aliyun.com", "utf-8"),
            URLEncoder.encode(slsUrl, "utf-8"),
            URLEncoder.encode(signInToken, "utf-8"));
        return signInUrl;
    }
}
