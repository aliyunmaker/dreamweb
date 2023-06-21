package cc.landingzone.dreamweb.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class CommonConstants {

    private static final Logger logger = LoggerFactory.getLogger(CommonConstants.class);

    public static final String Aliyun_AccessKeyId;

    public static final String Aliyun_AccessKeySecret;

    public static final String Aliyun_UserId;

    public static final String Aliyun_REGION_HANGZHOU = "cn-hangzhou";

    public static final String APPLICATION_TAG_KEY = "application";

    public static final String ENVIRONMENT_TYPE_TAG_KEY = "environmentType";

    public static final String[] ENVIRONMENT_TYPE_TAG_VALUES = {"product", "test"};

    public static final String DEFAULT_IMAGE_ID = "aliyun_3_x64_20G_alibase_20221102.vhd";
    public static final String DEFAULT_SECURITY_GROUP_ID = "sg-bp1j3v9i048rpldp8g20";
    public static final String DEFAULT_ECS_HOSTNAME = "ECS-test";
    public static final String DEFAULT_ECS_PASSWORD = "ECS@test1234";
    public static final String ECS_CHARGETYPE_POSTPAID = "PostPaid";
    public static final String ECS_CHARGETYPE_PREPAID = "PrePaid";
    public static final String DEFAULT_ECS_MAX_BANDWIDTH_OUT = "10";
    public static final String DEFAULT_ECS_SYSTEM_DISK_SIZE = "40";
    public static final String DEFAULT_ECS_SYSTEM_DISK_CATEGORY = "cloud_ssd";
    public static final String DEFAULT_ECS_DATA_DISK_SIZE = "100";
    public static final String DEFAULT_ECS_DATA_DISK_CATEGORY = "cloud_ssd";



    public static final String LOGOUT_SUCCESS_URL;

    // 是否线上环境
    public static final boolean ENV_ONLINE;

    public static final String CONFIG_PATH = System.getProperty("user.dir") + "/config/";

    static {
        Properties properties = loadProperties();
        ENV_ONLINE = Boolean.parseBoolean(properties.getProperty("dreamweb.env_online"));
        Aliyun_AccessKeyId = properties.getProperty("dreamweb.aliyun_accesskeyid");
        Aliyun_AccessKeySecret = properties.getProperty("dreamweb.aliyun_accesskeysecret");
        Aliyun_UserId = properties.getProperty("dreamweb.aliyun_userid");
        String logoutSuccessUrl = properties.getProperty("dreamweb.logout_success_url");
        if (StringUtils.isBlank(logoutSuccessUrl) || "<your_logout_success_url>".equals(logoutSuccessUrl)) {
            LOGOUT_SUCCESS_URL = "/login?logout";
        } else {
            LOGOUT_SUCCESS_URL = logoutSuccessUrl;
        }
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
