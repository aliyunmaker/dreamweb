package cc.landingzone.dreamcmp.common;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.TypeReference;
import com.aliyun.cloudsso20210515.models.ListDirectoriesResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;

import cc.landingzone.dreamcmp.common.utils.JsonUtils;
import cc.landingzone.dreamcmp.demo.akapply.KMSHelper;

public class CommonConstants {

    private static final Logger logger = LoggerFactory.getLogger(CommonConstants.class);
    
    
    public static final String Aliyun_Test_AccessKeyId = "LTAI5t6UJV1BTu2HcXz67ZWn";

    public static final String Aliyun_Test_AccessKeySecret = "iLYT7aps05nW5wVn2uPLX31Xb8yuEq";

    public static final String Aliyun_AccessKeyId;

    public static final String Aliyun_AccessKeySecret;

    public static final String Aliyun_TestAccount_AccessKeyId;

    public static final String Aliyun_TestAccount_AccessKeySecret;

    public static final String Aliyun_UserId;

    public static final String Aliyun_TestAccount_UserId;

    public static final String Aliyun_REGION_HANGZHOU = "cn-hangzhou";

    public static final String APPLICATION_TAG_KEY = "application";

    public static final String ENVIRONMENT_TYPE_TAG_KEY = "environmentType";

    public static final String[] ENVIRONMENT_TYPE_TAG_VALUES = {"product", "test"};

    // resource center admin name
    public static final String RESOURCE_CENTER_ADMIN_NAME = "管理账号";

    // log center的默认查询语句
    public static final String QUERY_STRING = "remote_addr:100.127.197.134";

    // 用于免密登录的RAM角色ARN
    public static final String ADMIN_ROLE_ARN = "acs:ram::1158528183198580:role/dreamcmp-admin";
//    public static final String ADMIN_ROLE_ARN = "acs:ram::1013026405737419:role/dreamweb-admin";

    // Config巡检规则前缀
    public static final String CONFIG_RULE_PREFIX = "dreamcmp-";

    public static final String DEFAULT_IMAGE_ID = "ubuntu_18_04_64_20G_alibase_20190624.vhd";
//    public static final String DEFAULT_SECURITY_GROUP_ID = "sg-bp103rdxtizwmfam0tfa";
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

    public static final String SLS_PROJECT_NAME = "dreamweb";

    public static final String STATUS_AVAILABLE = "Available";

    public static final String SCIM_KEY;
    public static final String SCIM_URL = "https://cloudsso-scim-cn-shanghai.aliyun.com/scim/v2";

    //example: {"admin":"admin","charles":"test"}
    public static final Map<String,String> LOGIN_USER_MAP;

    public static String  DKMSInstanceId;

    public static String  EncryptionKeyId;

    // 是否线上环境
    public static final boolean ENV_ONLINE;

    public static final String CONFIG_PATH = System.getProperty("user.dir") + "/config/";


    static {
        Properties properties = loadProperties();
        ENV_ONLINE = Boolean.parseBoolean(properties.getProperty("dreamcmp.env_online"));
        Aliyun_AccessKeyId = properties.getProperty("dreamcmp.aliyun_accesskeyid");
        Aliyun_AccessKeySecret = properties.getProperty("dreamcmp.aliyun_accesskeysecret");
        Aliyun_TestAccount_AccessKeyId = properties.getProperty("dreamcmp.aliyun_testaccount_accesskeyid");
        Aliyun_TestAccount_AccessKeySecret = properties.getProperty("dreamcmp.aliyun_testaccount_accesskeysecret");
        Aliyun_UserId = getCallerIdentity(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        Aliyun_TestAccount_UserId = getCallerIdentity(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);
        SCIM_KEY = properties.getProperty("dreamcmp.scim_key");
        String loginUserString = properties.getProperty("dreamcmp.login_user");
        LOGIN_USER_MAP = JsonUtils.parseObject(loginUserString, new TypeReference<Map<String, String>>() {});
        DKMSInstanceId = properties.getProperty("dreamcmp.dkms_instance_id");
        EncryptionKeyId = getEncryptionKeyId(DKMSInstanceId);
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
    
    
    public static void main(String[] args) {
        String Aliyun_UserId = getCallerIdentity(CommonConstants.Aliyun_AccessKeyId, CommonConstants.Aliyun_AccessKeySecret);
        String Aliyun_TestAccount_UserId = getCallerIdentity(CommonConstants.Aliyun_TestAccount_AccessKeyId, CommonConstants.Aliyun_TestAccount_AccessKeySecret);
        System.out.println(Aliyun_UserId);
        System.out.println(Aliyun_TestAccount_UserId);
       
    }

    /**
     * Get the Aliyun account ID of the current user
     */
    public static String getCallerIdentity(String accessKeyId, String accessKeySecret){
        try {
            com.aliyun.sts20150401.Client client = ClientHelper.createStsClient(accessKeyId, accessKeySecret);
            RuntimeOptions runtime = new RuntimeOptions();
            return client.getCallerIdentityWithOptions(runtime).getBody().getAccountId();
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return null;
        }
     }

     /**
      * Initialize SCIM，return the SCIM key
      */
     public static String initScim(){
        try {
            List<ListDirectoriesResponseBody.ListDirectoriesResponseBodyDirectories> directoriesList =
                    CloudSSOHelper.listDirectories();
            String directoryId;
            if (directoriesList != null && directoriesList.size() > 0) {
                directoryId = directoriesList.get(0).getDirectoryId();
            }else {
                directoryId = CloudSSOHelper.createDirectory("dreamweb");
            }
            List<String> scimServerCredentials = CloudSSOHelper.listSCIMServerCredentials(directoryId);
            for (String scimServerCredential : scimServerCredentials) {
                CloudSSOHelper.deleteSCIMServerCredential(directoryId, scimServerCredential);
            }
            return CloudSSOHelper.createSCIMServerCredential(directoryId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
     }

     public static String getEncryptionKeyId(String DKMSInstanceId){
         try {
             List<String> listKeys = KMSHelper.listKeys(DKMSInstanceId);
             if (listKeys.size() > 0) {
                 return listKeys.get(0);
             } else {
                 String tags = "[{\"TagKey\":\"CreatedBy\", \"TagValue\":\"dreamweb\"}]";
                 logger.info("Create a new key");
                 return KMSHelper.createKey(DKMSInstanceId, tags);
             }
         }catch (Exception e){
             logger.error(e.getMessage(), e);
             return null;
         }
     }

}
