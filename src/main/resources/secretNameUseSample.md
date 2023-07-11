使用教程（java）
1. 创建应用接入点，并为AAP绑定Client Key：https://help.aliyun.com/document_detail/295842.html?spm=a2c4g.190269.0.0.545837d6TVI6Xv#section-y8g-d85-c3g
2. 通过配置文件（secretsmanager.properties）构建客户端并测试使用

   a. 引入依赖
    ```xml
    <dependency>
        <groupId>com.aliyun</groupId>
        <artifactId>alibabacloud-secretsmanager-client</artifactId>
        <version>1.3.4</version>
    </dependency>
    <dependency>
        <groupId>com.aliyun</groupId>
        <artifactId>aliyun-java-sdk-core</artifactId>
        <version>4.5.17</version>
    </dependency>
    ```
   b. 编写配置文件 secretsmanager.properties
    ```properties
    ## 配置访问方式。
    credentials_type=client_key
    ## 读取Client Key的解密密码：支持从环境变量或者文件读取。
    #client_key_password_from_env_variable=KMS_PASSWORD
    client_key_password_from_file_path=clientKeyPassword
    ## 获取Client Key的私钥文件。
    client_key_private_key_path=ClientKey_KAAP.abd9354d-3873-4798-86dd-520bd7a8b927.json
    ## 配置关联的KMS地域。
    cache_client_region_id=[{"regionId":"cn-hangzhou"}]
    ```
   c. 构建客户端，并测试使用secretName创建oos bucket
    ```java
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
    ```