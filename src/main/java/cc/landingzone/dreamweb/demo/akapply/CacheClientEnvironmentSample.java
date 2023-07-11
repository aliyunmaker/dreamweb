package cc.landingzone.dreamweb.demo.akapply;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.oss20190517.AsyncClient;
import com.aliyun.sdk.service.oss20190517.models.CreateBucketConfiguration;
import com.aliyun.sdk.service.oss20190517.models.PutBucketRequest;
import com.aliyun.sdk.service.oss20190517.models.PutBucketResponse;
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClient;
import com.aliyuncs.kms.secretsmanager.client.SecretCacheClientBuilder;
import com.aliyuncs.kms.secretsmanager.client.model.SecretInfo;
import com.google.gson.Gson;
import darabonba.core.client.ClientOverrideConfiguration;
import java.util.concurrent.CompletableFuture;

public class CacheClientEnvironmentSample {
    public static void main(String[] args) {
        try {
            SecretCacheClient kmsClient = SecretCacheClientBuilder.newClient();
            String secretName = "acs/ram/user/tianyu";
            SecretInfo secretInfo = kmsClient.getSecretInfo(secretName);
            System.out.println(secretInfo);

            // example: create oss bucket
            JSONObject jsonObject = JSON.parseObject(secretInfo.getSecretValue());
            String accessKeyId = jsonObject.getString("AccessKeyId");
            String accessKeySecret = jsonObject.getString("AccessKeySecret");
            AsyncClient ossClient = createOssClient(accessKeyId,accessKeySecret);
            CreateBucketConfiguration createBucketConfiguration = CreateBucketConfiguration.builder()
                    .storageClass("Standard")
                    .dataRedundancyType("LRS")
                    .build();
            PutBucketRequest putBucketRequest = PutBucketRequest.builder()
                    .bucket("bucketname-" + System.currentTimeMillis())
                    .createBucketConfiguration(createBucketConfiguration)
                    .build();
            CompletableFuture<PutBucketResponse> response = ossClient.putBucket(putBucketRequest);
            PutBucketResponse resp = response.get();
            System.out.println(new Gson().toJson(resp));
            ossClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                ).build();
    }
}
