package cc.landingzone.dreamweb.sso;


import cc.landingzone.dreamweb.common.CommonConstants;
import cc.landingzone.dreamweb.model.enums.SSOSpEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

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

    static {
        Properties properties = CommonConstants.loadProperties();
        String idpEntityId = properties.getProperty("dreamweb.idp_entityid");
        if (StringUtils.isBlank(idpEntityId) || "<your_idp_entityid>".equals(idpEntityId)) {
            IDP_ENTITY_ID = "dreamweb.default";
        } else {
            IDP_ENTITY_ID = idpEntityId;
        }
    }

    public static String getSSOSpIdentifier(SSOSpEnum ssoSp) {
        switch (ssoSp) {
            case aliyun:
                return ALIYUN_IDENTIFIER;
            case aliyun_user:
                return ALIYUN_USER_IDENTIFIER;
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
    private static final String ALIYUN_USER_IDENTIFIER = "[aliyun user sso] must be replaced identifier";// "https://signin.aliyun.com/{uid}/saml/SSO";
    private static final String ALIYUN_USER_REPLY_URL = "[aliyun user sso] must be replaced replyUrl";// "https://signin.aliyun.com/saml/SSO";

    // aws role sso
    private static final String AWS_IDENTIFIER = "urn:amazon:webservices";
    private static final String AWS_REPLY_URL = "https://signin.aws.amazon.com/saml";
    private static final String AWS_ATTRIBUTE_KEY_ROLE_SESSION_NAME = "https://aws.amazon.com/SAML/Attributes/RoleSessionName";
    private static final String AWS_ATTRIBUTE_KEY_ROLE = "https://aws.amazon.com/SAML/Attributes/Role";

    // aws user sso
    private static final String AWS_USER_IDENTIFIER = "[aws user sso] must be replaced identifier";
    private static final String AWS_USER_REPLY_URL = "[aws user sso] must be replaced replyUrl";// "https://us-east-2.signin.aws.amazon.com/platform/saml/acs/6b9f0caa-6290-41d0-b4de-bd190139324e";

}
