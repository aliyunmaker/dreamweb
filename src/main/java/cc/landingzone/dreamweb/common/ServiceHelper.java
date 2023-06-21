package cc.landingzone.dreamweb.common;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.oss20190517.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ServiceHelper {
    public static com.aliyun.ecs20140526.Client createEcsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("ecs.aliyuncs.com");
        return new com.aliyun.ecs20140526.Client(config);
    }

    public static com.aliyun.sdk.service.oss20190517.AsyncClient createOssClient(String accessKeyId, String accessKeySecret) throws Exception {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());

        return AsyncClient.builder()
                .region(CommonConstants.Aliyun_REGION_HANGZHOU)
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("oss-cn-hangzhou.aliyuncs.com")
                )
                .build();
    }

    public static com.aliyun.slb20140515.Client createSlbClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("slb.aliyuncs.com");
        return new com.aliyun.slb20140515.Client(config);
    }

    public static com.aliyun.rds20140815.Client createRdsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("rds.aliyuncs.com");
        return new com.aliyun.rds20140815.Client(config);
    }

    public static com.aliyun.sls20201230.Client createSlsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("cn-hangzhou.log.aliyuncs.com");
        return new com.aliyun.sls20201230.Client(config);
    }

    public static com.aliyun.ram20150501.Client createRamClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                  .setAccessKeyId(accessKeyId)
                  .setAccessKeySecret(accessKeySecret);
        config.endpoint = "ram.aliyuncs.com";
        return new com.aliyun.ram20150501.Client(config);
    }

    public static com.aliyun.tag20180828.Client createTagClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "tag.aliyuncs.com";
        return new com.aliyun.tag20180828.Client(config);
    }

    /**
     * 拼接资源ARN
     * @param resourceType:oss、log
     * @param resourceNameList
     * @return
     */
    public static List<String> getResourceArn(String resourceType, List<String> resourceNameList, String accountId){
        List<String> resourceArn = new ArrayList<>();
        switch (resourceType){
            case "oss":
                for (String resourceName : resourceNameList) {
                    resourceArn.add("acs:oss:*:" + accountId + ":" + resourceName);
                }
                break;
            case "log":
                for (String resourceName : resourceNameList) {
                    resourceArn.add("acs:log:*:" + accountId + ":project/" + resourceName);
                }
                break;
            default:
                break;
        }
        return resourceArn;
    }

    /**
     * 拼接RAM用户的ARN：acs:ram::<account-id>:user/<user-name>
     * @param userName
     * @param accountId
     * @return
     */
    public static String getRamArn(String userName,String accountId){
        return "acs:ram::" + accountId + ":user/" + userName;
    }


}
