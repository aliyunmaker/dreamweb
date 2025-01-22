
## SDK V2


```xml
<!-- Requires: version >= 0.3.4 -->
<!-- 推荐使用最新版本 -->
<!-- 获取所有已发布的版本列表，请参见https://github.com/aliyun/credentials-java/blob/master/ChangeLog.txt -->
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>credentials-java</artifactId>
    <version>0.3.4</version>
</dependency>
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>sts20150401</artifactId>
  <version>1.1.4</version>
</dependency>
```


```java
import com.alibaba.fastjson.JSON;
import com.aliyun.credentials.Client;
import com.aliyun.credentials.models.Config;

public class AliyunCredentialDemoSDKV2 {

    public static void main(String[] args) throws Exception {
        sdkv2_longterm_credential();
        sdkv2_ecs_instance_role();
    }
  
    // SDKV2:纯明文AK模式
    public static void sdkv2_longterm_credential() throws Exception {
        Config config = new Config();
        config.setType("access_key");
        config.setAccessKeyId("LTAI5tRUR9ZQdfZAm*******");
        config.setAccessKeySecret("BuLBPFaStNihGtdNSMGYeYiu*******");
        Client credentialClient = new Client(config);

        com.aliyun.teaopenapi.models.Config clientConfig = new com.aliyun.teaopenapi.models.Config();
        clientConfig.setCredential(credentialClient);
        clientConfig.setEndpoint("sts.cn-hangzhou.aliyuncs.com");
        com.aliyun.sts20150401.Client stsClient = new com.aliyun.sts20150401.Client(clientConfig);
        com.aliyun.sts20150401.models.GetCallerIdentityResponse getCallerIdentityResponse =
            stsClient.getCallerIdentity();
        System.out.println(JSON.toJSONString(getCallerIdentityResponse));
    }

    // SDKV2:ecs instance profile
    public static void sdkv2_ecs_instance_role() throws Exception {

        Config config = new Config();
        config.setType("ecs_ram_role");
        config.setRoleName("ecs-demo-role");
        Client credentialClient = new Client(config);

        com.aliyun.teaopenapi.models.Config clientConfig = new com.aliyun.teaopenapi.models.Config();
        clientConfig.setCredential(credentialClient);
        clientConfig.setEndpoint("sts.cn-hangzhou.aliyuncs.com");
        com.aliyun.sts20150401.Client stsClient = new com.aliyun.sts20150401.Client(clientConfig);
        com.aliyun.sts20150401.models.GetCallerIdentityResponse getCallerIdentityResponse =
            stsClient.getCallerIdentity();
        System.out.println(JSON.toJSONString(getCallerIdentityResponse));
    }
}
```
----

## SDK V1

```xml
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.7.1</version>
</dependency>
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-sts</artifactId>
  <version>3.1.2</version>
</dependency>
<!-- Requires: version >= 0.3.4 -->
<!-- 推荐使用最新版本 -->
<!-- 获取所有已发布的版本列表，请参见https://github.com/aliyun/credentials-java/blob/master/ChangeLog.txt -->
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>credentials-java</artifactId>
    <version>0.3.4</version>
</dependency>
```

```java
import com.alibaba.fastjson.JSON;
import com.aliyun.credentials.Client;
import com.aliyun.credentials.models.Config;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.auth.AlibabaCloudCredentials;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.auth.BasicCredentials;
import com.aliyuncs.auth.BasicSessionCredentials;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.sts.model.v20150401.GetCallerIdentityRequest;
import com.aliyuncs.sts.model.v20150401.GetCallerIdentityResponse;

public class AliyunCredentialDemoSDKV1 {

    public static void main(String[] args) throws Exception {
        sdkv1_longterm_credential();
        sdkv1_ecs_instance_role(args);
    }

    // SDKV1: long term credentail
    public static void sdkv1_longterm_credential() throws Exception {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou");
        IAcsClient acsClient = new DefaultAcsClient(profile,
            new BasicCredentials("LTAI5tRUR9ZQdfZAm*******", "BuLBPFaStNihGtdNSMGYeYi*******"));
        GetCallerIdentityRequest getCallerIdentityRequest = new GetCallerIdentityRequest();
        GetCallerIdentityResponse getCallerIdentityResponse = acsClient.getAcsResponse(getCallerIdentityRequest);
        System.out.println(JSON.toJSONString(getCallerIdentityResponse));

    }

    // SDKV1: ecs instance profile
    public static void sdkv1_ecs_instance_role(String[] args) throws Exception {
        Config config = new Config();
        config.setType("ecs_ram_role");
        config.setRoleName("ecs-demo-role");
        Client credentialClient = new Client(config);
        IAcsClient acsClient =
            new DefaultAcsClient(DefaultProfile.getProfile("cn-hangzhou"), new AlibabaCloudCredentialsProvider() {

                @Override
                public AlibabaCloudCredentials getCredentials() throws ClientException, ServerException {
                    BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
                        credentialClient.getCredential().getAccessKeyId(),
                        credentialClient.getCredential().getAccessKeySecret(),
                        credentialClient.getCredential().getSecurityToken());
                    return sessionCredentials;
                }

            });

        GetCallerIdentityRequest getCallerIdentityRequest = new GetCallerIdentityRequest();
        GetCallerIdentityResponse getCallerIdentityResponse = acsClient.getAcsResponse(getCallerIdentityRequest);
        System.out.println(JSON.toJSONString(getCallerIdentityResponse));
    }
}
```

