package cc.landingzone.dreamweb.common;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonConstants {

    private static final Logger logger = LoggerFactory.getLogger(CommonConstants.class);

    public static final String Aliyun_AccessKeyId;

    public static final String Aliyun_AccessKeySecret;

    public static final String Aliyun_REGION_HANGZHOU = "cn-hangzhou";

    public static final String WEB_LANDINGZONE_ID;
    public static final String WEB_LANDINGZONE_SECRET;

    public static final String API_LOGOUT_URL = "/apiLogout";
    public static final String API_LOGOUT_SUCCESS_URL;

    public static final String TEST_API_ACCESS_KEY_ID;
    public static final String TEST_API_ACCESS_KEY_SECRET;

    // 是否线上环境
    public static final boolean ENV_ONLINE;

    static {
        Properties properties = loadProperties();
        ENV_ONLINE = Boolean.parseBoolean(properties.getProperty("dreamweb.env_online"));
        Aliyun_AccessKeyId = properties.getProperty("dreamweb.aliyun_accesskeyid");
        Aliyun_AccessKeySecret = properties.getProperty("dreamweb.aliyun_accesskeysecret");
        WEB_LANDINGZONE_ID = properties.getProperty("dreamweb.weixin.landingzone.id");
        WEB_LANDINGZONE_SECRET = properties.getProperty("dreamweb.weixin.landingzone.secret");
        API_LOGOUT_SUCCESS_URL = properties.getProperty("dreamweb.api_logout_success_url");

        // 测试使用
        TEST_API_ACCESS_KEY_ID = properties.getProperty("dreamweb.test.api_accesskeyid");
        TEST_API_ACCESS_KEY_SECRET = properties.getProperty("dreamweb.test.api_accesskeysecret");
    }

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream ins = CommonConstants.class.getResourceAsStream("/application.properties");
            properties.load(ins);
            ins.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return properties;
    }

}
