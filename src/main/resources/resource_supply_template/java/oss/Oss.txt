package cc.landingzone.dreamweb;

import com.alibaba.fastjson.JSON;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.oss20190517.AsyncClient;
import com.aliyun.sdk.service.oss20190517.models.CreateBucketConfiguration;
import com.aliyun.sdk.service.oss20190517.models.PutBucketRequest;
import com.aliyun.sdk.service.oss20190517.models.PutBucketResponse;
import com.aliyun.tag20180828.models.TagResourcesRequest;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Oss {

    public static void main(String[] args) throws Exception {
        String accountId = System.getenv("ALIBABA_CLOUD_ACCOUNT_ID");
        String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET");

        String bucketName = "dreamwebtest";
        // 创建bucket
        AsyncClient client = createOssClient(accessKeyId,accessKeySecret);
        CreateBucketConfiguration createBucketConfiguration = CreateBucketConfiguration.builder()
                .storageClass("Standard")
                .dataRedundancyType("LRS")
                .build();
        PutBucketRequest putBucketRequest = PutBucketRequest.builder()
                .bucket(bucketName)
                .createBucketConfiguration(createBucketConfiguration)
                .build();
        CompletableFuture<PutBucketResponse> response = client.putBucket(putBucketRequest);
        PutBucketResponse resp = response.get();
        System.out.println(new Gson().toJson(resp));
        client.close();

        // 添加标签
        com.aliyun.tag20180828.Client tagClient = createTagClient(accessKeyId, accessKeySecret);
        Map<String, String> tags = new HashMap<>();
        tags.put("application", "application1");
        tags.put("environmentType", "product");
        String tagStr = JSON.toJSONString(tags);
        List<String> resourceArn = new ArrayList<>();
        resourceArn.add("acs:oss:*:" + accountId + ":bucket/" + bucketName);
        TagResourcesRequest tagResourcesRequest = new TagResourcesRequest()
                .setResourceARN(resourceArn)
                .setTags(tagStr)
                .setRegionId("cn-hangzhou");
        RuntimeOptions runtime = new RuntimeOptions();
        tagClient.tagResourcesWithOptions(tagResourcesRequest, runtime);
    }

    public static AsyncClient createOssClient(String accessKeyId, String accessKeySecret) throws Exception {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(accessKeyId)
                .accessKeySecret(accessKeySecret)
                .build());
        return AsyncClient.builder()
                .region("cn-hangzhou")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("oss-cn-hangzhou.aliyuncs.com")
                )
                .build();
    }

    public static com.aliyun.tag20180828.Client createTagClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "tag.aliyuncs.com";
        return new com.aliyun.tag20180828.Client(config);
    }

}