package cc.landingzone.dreamcmp.common.config;

import cc.landingzone.dreamcmp.common.CommonConstants;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import com.aliyun.credentials.models.CredentialModel;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentials;
import org.springframework.context.annotation.Profile;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Configuration
public class OssClientConfig {

    @Value("${dreamcmp.workshop.oss_region}")
    private String ossRegion;

    @Autowired(required = false)
    private com.aliyun.credentials.Client credentialClient;

    @Bean
    OSS ossClient() {
        String endpoint = String.format("https://oss-%s.aliyuncs.com", ossRegion);
        return new OSSClientBuilder().build(
            endpoint,
            CommonConstants.Aliyun_TestAccount_AccessKeyId,
            CommonConstants.Aliyun_TestAccount_AccessKeySecret,
            CommonConstants.Aliyun_TestAccount_SecurityToken
        );
    }

    @Bean
    @Profile("!dev")
    OSS ossClientEcsRole() {
        String endpoint = String.format("https://oss-%s.aliyuncs.com", ossRegion);

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
