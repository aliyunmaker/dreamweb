package cc.landingzone.dreamweb.common;

import cc.landingzone.dreamweb.demo.ssologin.model.SSOUserRole;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CommonConstants {

    private static final Logger logger = LoggerFactory.getLogger(CommonConstants.class);

    public static final String Aliyun_AccessKeyId;

    public static final String Aliyun_AccessKeySecret;

    public static final String Aliyun_UserId;
    public static final String AWS_UserId;
    public static final String Aliyun_SSO_UserId;

    public static final String Aliyun_REGION_HANGZHOU = "cn-hangzhou";

    public static final String APPLICATION_TAG_KEY = "application";

    public static final String ENVIRONMENT_TYPE_TAG_KEY = "environmentType";

    public static final String[] ENVIRONMENT_TYPE_TAG_VALUES = {"product", "test"};

    // SSO登录的用户和角色列表
    public static final List<SSOUserRole> SSO_LOGIN_USER_IDS = new ArrayList<>();
    public static final List<SSOUserRole> SSO_LOGIN_ROLE_IDS = new ArrayList<>();
    public static final List<SSOUserRole> SSO_LOGIN_CLOUD_USER_IDS = new ArrayList<>();

    // 用于免密登录的RAM角色ARN
    public static final String ADMIN_ROLE_ARN = "acs:ram::1013026405737419:role/dreamweb-admin";

    public static final String DEFAULT_IMAGE_ID = "ubuntu_18_04_64_20G_alibase_20190624.vhd";
    public static final String DEFAULT_SECURITY_GROUP_ID = "sg-bp103rdxtizwmfam0tfa";
    public static final String DEFAULT_ECS_HOSTNAME = "ECS-test-";
    public static final String DEFAULT_ECS_PASSWORD = "ECS@test1234";
    public static final String ECS_CHARGETYPE_POSTPAID = "PostPaid";
    public static final String ECS_CHARGETYPE_PREPAID = "PrePaid";
    public static final String DEFAULT_ECS_MAX_BANDWIDTH_OUT = "10";
    public static final String DEFAULT_ECS_SYSTEM_DISK_SIZE = "40";
    public static final String DEFAULT_ECS_SYSTEM_DISK_CATEGORY = "cloud_ssd";
    public static final String DEFAULT_ECS_DATA_DISK_SIZE = "100";
    public static final String DEFAULT_ECS_DATA_DISK_CATEGORY = "cloud_ssd";
    public static final String VSWITCH_RESOURCETYPE = "ALIYUN::VPC::VSWITCH";

    public static final String VPC_RESOURCETYPE = "ALIYUN::VPC::VPC";

    public static final String LOGOUT_SUCCESS_URL;

    // 是否线上环境
    public static final boolean ENV_ONLINE;

    public static final String CONFIG_PATH = System.getProperty("user.dir") + "/config/";

    static {
        Properties properties = loadProperties();
        addSSOUsersRoles();
        ENV_ONLINE = Boolean.parseBoolean(properties.getProperty("dreamweb.env_online"));
        Aliyun_AccessKeyId = properties.getProperty("dreamweb.aliyun_accesskeyid");
        Aliyun_AccessKeySecret = properties.getProperty("dreamweb.aliyun_accesskeysecret");
        Aliyun_UserId = properties.getProperty("dreamweb.aliyun_userid");
        AWS_UserId = properties.getProperty("dreamweb.aws_userid");
        Aliyun_SSO_UserId = properties.getProperty("dreamweb.aliyun_sso_userid");
        String logoutSuccessUrl = properties.getProperty("dreamweb.logout_success_url");
        if (StringUtils.isBlank(logoutSuccessUrl) || "<your_logout_success_url>".equals(logoutSuccessUrl)) {
            LOGOUT_SUCCESS_URL = "/login?logout";
        } else {
            LOGOUT_SUCCESS_URL = logoutSuccessUrl;
        }
    }

    public static void addSSOUsersRoles() {
        SSOUserRole aliyunRole = new SSOUserRole("aliyun", "管理员", "kidccc@gmail.com", "dreamweb-test-role");
        SSO_LOGIN_ROLE_IDS.add(aliyunRole);
        SSOUserRole awsRole = new SSOUserRole("aws", "管理员", "me@chengchao.name", "myrole");
        SSO_LOGIN_ROLE_IDS.add(awsRole);
        SSOUserRole tencentRole = new SSOUserRole("tencent", "管理员", "100000543428", "superadmin");
        SSO_LOGIN_ROLE_IDS.add(tencentRole);

        SSOUserRole aliyunUser = new SSOUserRole("aliyun", "云效账号", "kidccc@gmail.com", "test-user");
        SSO_LOGIN_USER_IDS.add(aliyunUser);
        SSOUserRole awsUser = new SSOUserRole("aws", "Identity Center-个人账号", "kenmako555@gmail.com", "kenmako555@gmail.com");
        SSO_LOGIN_USER_IDS.add(awsUser);
        SSOUserRole tencentUser = new SSOUserRole("tencent", "个人账号", "100000543428", "chengchao");
        SSO_LOGIN_USER_IDS.add(tencentUser);

        SSOUserRole ssoCloudUser = new SSOUserRole("aliyun", "CloudSSO-管理员", "20210603demo1", "tianyu");
        SSO_LOGIN_CLOUD_USER_IDS.add(ssoCloudUser);
    }

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream ins = CommonConstants.class.getResourceAsStream("/application.properties");
            if (ins == null) {
                ins = new FileInputStream(CommonConstants.CONFIG_PATH + "/application.properties");
            }
            properties.load(ins);
            ins.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return properties;
    }

}
