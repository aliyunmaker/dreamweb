package cc.landingzone.dreamweb.common;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.oss20190517.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;

/**
 * 作者：珈贺
 * 描述：
 */
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
}
