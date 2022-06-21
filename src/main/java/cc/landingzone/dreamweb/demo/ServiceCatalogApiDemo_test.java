package cc.landingzone.dreamweb.demo;

import cc.landingzone.dreamweb.common.EndpointEnum;
import cc.landingzone.dreamweb.model.User;
import cc.landingzone.dreamweb.model.UserRole;
import cc.landingzone.dreamweb.model.enums.SSOSpEnum;
import cc.landingzone.dreamweb.service.UserRoleService;
import cc.landingzone.dreamweb.service.UserService;
import cc.landingzone.dreamweb.sso.SSOConstants;
import cc.landingzone.dreamweb.sso.SamlGenerator;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.*;

public class ServiceCatalogApiDemo_test {
    private static final String REGION = "cn-hangzhou";
    @Resource
    private static UserService userService;

    @Resource
    private static UserRoleService userRoleService;

    public static void main(String[] args) throws Exception {
        String stsEndpoint = "sts.cn-hangzhou.aliyuncs.com";


        // 获取当前用户信息以及所需要使用的ram角色信息
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        String userName = "dou";
        Integer roleId = 1;
        User user = userService.getUserByLoginName(userName);
        UserRole userRole = userRoleService.getUserRoleById(roleId);

        // 得到roleArn和idpArn，生成Saml Assertion
        String[] roleValue = userRole.getRoleValue().split(",");
        String roleArn = roleValue[0];
        String samlProviderArn = roleValue[1];
        String samlAssertion = getSamlAssertion(user, userRole);

        // 访问令牌服务获取临时AK和Token
        CommonResponse commonResponse = requestAccessKeyAndSecurityToken(REGION, roleArn, samlProviderArn,
                samlAssertion, stsEndpoint);
        JSONObject assumeRole = JSONObject.parseObject(commonResponse.getData());
        JSONObject credentials = assumeRole.getJSONObject("Credentials");
        System.out.println(credentials);

    }

    private static String getSamlAssertion(User user, UserRole userRole) throws Exception {

        UserRoleService userRoleService = new UserRoleService();

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

    private static CommonResponse requestAccessKeyAndSecurityToken(String region, String roleArn, String samlProviderArn,
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
        System.out.println(response.getData());
        return response;
    }
}
