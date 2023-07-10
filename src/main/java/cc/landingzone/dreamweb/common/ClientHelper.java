package cc.landingzone.dreamweb.common;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.oss20190517.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;

/**
 * Author：珈贺
 * Description：
 */
public class ClientHelper {

    public static com.aliyun.ecs20140526.Client createEcsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint("ecs-cn-hangzhou.aliyuncs.com");
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

    public static com.aliyun.sts20150401.Client createStsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "sts.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.sts20150401.Client(config);
    }

    public static com.aliyun.actiontrail20200706.Client createTrailClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "actiontrail.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.actiontrail20200706.Client(config);
    }

    public static com.aliyun.vpc20160428.Client createVpcClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "vpc.aliyuncs.com";
        return new com.aliyun.vpc20160428.Client(config);
    }

    public static com.aliyun.resourcemanager20200331.Client createResourceManagerClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "resourcemanager.aliyuncs.com";
        return new com.aliyun.resourcemanager20200331.Client(config);
    }

    public static com.aliyun.resourcecenter20221201.Client createResourceCenterClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "resourcecenter.aliyuncs.com";
        return new com.aliyun.resourcecenter20221201.Client(config);
    }

    public static com.aliyun.kms20160120.Client createKmsClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "kms.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.kms20160120.Client(config);
    }
}
