## OSS SDK

```xml
<!-- Requires: version >= 0.3.4 -->
<!-- 推荐使用最新版本 -->
<!-- 获取所有已发布的版本列表，请参见https://github.com/aliyun/credentials-java/blob/master/ChangeLog.txt -->
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>credentials-java</artifactId>
    <version>0.3.4</version>
</dependency>
<!--oss-sdk-->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.17.4</version>
</dependency>
```

```java
@Configuration
public class CredentialConfig {

    // 初始化凭据客户端
    // 您可以在代码中显式配置，来初始化凭据客户端
    // 荐您使用该方式，在代码中明确指定实例角色，避免运行环境中的环境变量、配置文件等带来非预期的结果。
    @Bean
    @Profile("!dev")
    Client credentialClient() {
       Config config = new Config()
           .setType("ecs_ram_role")
           // 选填，该ECS实例角色的角色名称，不填会自动获取，建议加上以减少请求次数
           .setRoleName("<请填写ECS实例角色的角色名称>")
           // 在加固模式下获取STS Token，强烈建议开启
           .setEnableIMDSv2(true);
       return new Client(config);
    }
}
```


```java
@Configuration
public class OssClientConfig {

    @Autowired(required = false)
    private com.aliyun.credentials.Client credentialClient;

    // 使用 AK、SK 初始化OSS客户端
    @Bean
    OSS ossClient() {
        return new OSSClientBuilder().build(
            "<请填写OSS的Endpoint>",
            "<请填写AccessKeyId>",
            "<请填写AccessKeySecret>"
        );
    }

    // 使用 Credentials 初始化OSS客户端
    @Bean
    @Profile("!dev")
    OSS ossClientEcsRole() {
        String endpoint = "<请填写OSS的Endpoint>";

        // 建议使用更安全的V4签名算法，则初始化时需要加入endpoint对应的region信息，同时声明SignVersion.V4
        // OSS Java SDK 3.17.4及以上版本支持V4签名。
        ClientBuilderConfiguration configuration = new ClientBuilderConfiguration();
        configuration.setSignatureVersion(SignVersion.V4);

        return OSSClientBuilder.create()
            .endpoint(endpoint)
            .clientConfiguration(configuration)
            .region(ossRegion)
            .credentialsProvider(new CredentialsProvider() {
                @Override
                public void setCredentials(Credentials credentials) {
                }

                @Override
                public Credentials getCredentials() {
                    // 保证线程安全，从 CredentialModel 中获取 ak/sk/security token
                    CredentialModel credentialModel = credentialClient.getCredential();
                    String ak = credentialModel.getAccessKeyId();
                    String sk = credentialModel.getAccessKeySecret();
                    String token = credentialModel.getSecurityToken();
                    return new DefaultCredentials(ak, sk, token);
                }
            }).build();
    }
}
```
