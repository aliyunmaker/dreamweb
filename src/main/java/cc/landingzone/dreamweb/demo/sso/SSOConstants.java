package cc.landingzone.dreamweb.demo.sso;


import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.common.model.enums.SSOSpEnum;
import cc.landingzone.dreamweb.demo.ssologin.model.SSOUserRole;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SSOConstants {
    // 是否自动提交表单,默认为false,方便调试
    public static final boolean SSO_FORM_AUTO_SUBMIT = true;

    public static final String PUBLIC_KEY_PATH = "/ssocert/saml.crt";
    public static final String PRIVATE_KEY_PATH = "/ssocert/saml.pkcs8";

//    // 用户名: 一般是员工的邮箱
//    public static final String ROLE_SESSION_NAME = "admin@example.name";
//
//    // role的list,格式如下注释
//    // acs:ram::{uid}:role/{rolename},acs:ram::{uid}:saml-provider/{idp_provider_name}
//    public static final List<String> ROLE_LIST = Arrays.asList(
//            "acs:ram::1764263140474643:role/super3,acs:ram::1764263140474643:saml-provider/superAD",
//            "acs:ram::1764263140474643:role/super2,acs:ram::1764263140474643:saml-provider/superAD");

    // IDP_ENTITY_ID 唯一ID,代表IDP
    public static final String IDP_ENTITY_ID;

    // SSO登录的用户和角色列表
    public static final List<SSOUserRole> SSO_LOGIN_ROLE_IDS = new ArrayList<>();
    public static final List<SSOUserRole> SSO_LOGIN_USER_IDS = new ArrayList<>();
    public static final List<SSOUserRole> SSO_LOGIN_CLOUD_USER_IDS = new ArrayList<>();

    // SSO登录的User Principal Name和角色对应的ARN
    public static final Map<String, String> ALIYUN_SSO_LOGIN_USER_PRINCIPAL_NAME = new HashMap<>();
    public static final Map<String, String> ALIYUN_SSO_LOGIN_ROLE_ID_ARN = new HashMap<>();
    public static final Map<String, String> AWS_SSO_LOGIN_ROLE_ID_ARN = new HashMap<>();

    static {
        Properties properties = CommonConstants.loadProperties();
        addSSOUsersRoles();
        String idpEntityId = properties.getProperty("dreamweb.idp_entityid");
        if (StringUtils.isBlank(idpEntityId) || "<your_idp_entityid>".equals(idpEntityId)) {
            IDP_ENTITY_ID = "dreamweb.default";
        } else {
            IDP_ENTITY_ID = idpEntityId;
        }
    }

    public static void addSSOUsersRoles() {
        SSOUserRole aliyunRole = new SSOUserRole("aliyun", "管理员", "kidccc@gmail.com", "dreamweb-test-role");
        SSO_LOGIN_ROLE_IDS.add(aliyunRole);
        ALIYUN_SSO_LOGIN_ROLE_ID_ARN.put("dreamweb-test-role", "acs:ram::1426447221208365:role/dreamweb-test-role,acs:ram::1426447221208365:saml-provider/dreamweb.test");

        SSOUserRole awsRole = new SSOUserRole("aws", "管理员", "me@chengchao.name", "myrole");
        SSO_LOGIN_ROLE_IDS.add(awsRole);
        AWS_SSO_LOGIN_ROLE_ID_ARN.put("myrole", "arn:aws:iam::626847370191:role/myrole,arn:aws:iam::626847370191:saml-provider/dreamweb");

        SSOUserRole tencentRole = new SSOUserRole("tencent", "管理员", "100000543428", "superadmin");
        SSO_LOGIN_ROLE_IDS.add(tencentRole);

        SSOUserRole aliyunUser = new SSOUserRole("aliyun", "云效账号", "kidccc@gmail.com", "test-user");
        SSO_LOGIN_USER_IDS.add(aliyunUser);
        ALIYUN_SSO_LOGIN_USER_PRINCIPAL_NAME.put("test-user", "test-user@1426447221208365.onaliyun.com");

        SSOUserRole awsUser = new SSOUserRole("aws", "Identity Center-个人账号", "kenmako555@gmail.com", "kenmako555@gmail.com");
        SSO_LOGIN_USER_IDS.add(awsUser);
        SSOUserRole tencentUser = new SSOUserRole("tencent", "个人账号", "100000543428", "chengchao");
        SSO_LOGIN_USER_IDS.add(tencentUser);

        SSOUserRole ssoCloudUser = new SSOUserRole("aliyun", "CloudSSO-管理员", "20210603demo1", "tianyu");
        SSO_LOGIN_CLOUD_USER_IDS.add(ssoCloudUser);
    }

    public static String getSSOSpIdentifier(SSOSpEnum ssoSp) {
        switch (ssoSp) {
            case aliyun:
                return ALIYUN_IDENTIFIER;
            case aliyun_user:
                return ALIYUN_USER_IDENTIFIER;
            case aliyun_user_cloudsso:
                return ALIYUN_USER_CLOUDSSO_IDENTIFIER;
            case aws:
                return AWS_IDENTIFIER;
            case aws_user:
                return AWS_USER_IDENTIFIER;
            default:
                throw new RuntimeException("not support type:" + ssoSp);
        }
    }

    public static String getSSOSpReplyUrl(SSOSpEnum ssoSp) {
        switch (ssoSp) {
            case aliyun:
                return ALIYUN_REPLY_URL;
            case aliyun_user:
                return ALIYUN_USER_REPLY_URL;
            case aliyun_user_cloudsso:
                return ALIYUN_USER_CLOUDSSO_REPLY_URL;
            case aws:
                return AWS_REPLY_URL;
            case aws_user:
                return AWS_USER_REPLY_URL;
            default:
                throw new RuntimeException("not support type:" + ssoSp);
        }
    }

    public static String getSSOSpAttributeKeyRoleSessionName(SSOSpEnum ssoSp) {
        switch (ssoSp) {
            case aliyun:
                return ALIYUN_ATTRIBUTE_KEY_ROLE_SESSION_NAME;
            case aws:
                return AWS_ATTRIBUTE_KEY_ROLE_SESSION_NAME;
            case aliyun_user:
                // 这里留空
                return "";
            case aws_user:
                return "";
            default:
                throw new RuntimeException("not support type:" + ssoSp);
        }
    }

    public static String getSSOSpAttributeKeyRole(SSOSpEnum ssoSp) {
        switch (ssoSp) {
            case aliyun:
                return ALIYUN_ATTRIBUTE_KEY_ROLE;
            case aws:
                return AWS_ATTRIBUTE_KEY_ROLE;
            case aliyun_user:
                // 这里留空
                return "";
            case aws_user:
                return "";
            default:
                throw new RuntimeException("not support type:" + ssoSp);
        }
    }

    /**
     * 以下内容不需要修改
     */
//    public static final String SSO_SP_IDENTIFIER = "urn:alibaba:cloudcomputing";
//    public static final String SSO_SP_REPLY_URL = "https://signin.aliyun.com/saml-role/sso";
//    public static final String SSO_SP_ATTRIBUTE_KEY_ROLE_SESSION_NAME = "https://www.aliyun.com/SAML-Role/Attributes/RoleSessionName";
//    public static final String SSO_SP_ATTRIBUTE_KEY_ROLE = "https://www.aliyun.com/SAML-Role/Attributes/Role";

//  public static final List<String> ROLE_LIST = Arrays.asList(
//  "arn:aws:iam::433312851566:role/myrole1,arn:aws:iam::433312851566:saml-provider/springrun");
//

    // aliyun role sso
    private static final String ALIYUN_IDENTIFIER = "urn:alibaba:cloudcomputing";
    private static final String ALIYUN_REPLY_URL = "https://signin.aliyun.com/saml-role/sso";
    private static final String ALIYUN_ATTRIBUTE_KEY_ROLE_SESSION_NAME = "https://www.aliyun.com/SAML-Role/Attributes/RoleSessionName";
    private static final String ALIYUN_ATTRIBUTE_KEY_ROLE = "https://www.aliyun.com/SAML-Role/Attributes/Role";

    // aliyun user sso
    private static final String ALIYUN_USER_IDENTIFIER = "https://signin.aliyun.com/{uid}/saml/SSO";
    private static final String ALIYUN_USER_REPLY_URL = "https://signin.aliyun.com/saml/SSO";

    // aliyun user cloud sso
    private static final String ALIYUN_USER_CLOUDSSO_IDENTIFIER = "https://signin-cn-shanghai.alibabacloudsso.com/saml/sp/d-00ku3dzn9l01";
    private static final String ALIYUN_USER_CLOUDSSO_REPLY_URL = "https://signin-cn-shanghai.alibabacloudsso.com/saml/acs/35f4bddf-2d3c-4347-b520-2cb7241e7e6d";

    // aws role sso
    private static final String AWS_IDENTIFIER = "urn:amazon:webservices";
    private static final String AWS_REPLY_URL = "https://signin.aws.amazon.com/saml";
    private static final String AWS_ATTRIBUTE_KEY_ROLE_SESSION_NAME = "https://aws.amazon.com/SAML/Attributes/RoleSessionName";
    private static final String AWS_ATTRIBUTE_KEY_ROLE = "https://aws.amazon.com/SAML/Attributes/Role";

    // aws user sso
//    private static final String AWS_USER_IDENTIFIER = "[aws user sso] must be replaced identifier";
//    private static final String AWS_USER_REPLY_URL = "[aws user sso] must be replaced replyUrl";// "https://us-east-2.signin.aws.amazon.com/platform/saml/acs/6b9f0caa-6290-41d0-b4de-bd190139324e";
    private static final String AWS_USER_IDENTIFIER = "https://us-east-1.signin.aws.amazon.com/platform/saml/d-90679adb49";
    private static final String AWS_USER_REPLY_URL = "https://us-east-1.signin.aws.amazon.com/platform/saml/acs/d4563f17-f40b-4e75-ab03-224b7162c54e";

    public static String getSSOSpUserId(SSOSpEnum ssoSp) {
        switch (ssoSp) {
            case aliyun_user:
                return CommonConstants.Aliyun_UserId;
            case aliyun:
            case aliyun_user_cloudsso:
            case aws:
            case aws_user:
            default:
                throw new RuntimeException("not support type:" + ssoSp);
        }
    }
}
