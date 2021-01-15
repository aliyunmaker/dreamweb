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


    // 是否线上环境
    public static final boolean ENV_ONLINE;

    static {
        Properties properties = loadProperties();
        ENV_ONLINE = Boolean.parseBoolean(properties.getProperty("env_online"));
        Aliyun_AccessKeyId = properties.getProperty("aliyun_accesskeyid");
        Aliyun_AccessKeySecret = properties.getProperty("aliyun_accesskeysecret");
    }

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try {
            InputStream ins = CommonConstants.class.getResourceAsStream("/dreamweb.properties");
            properties.load(ins);
            ins.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return properties;
    }

}
