package cc.landingzone.dreamcmp.common.config;

import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.EndpointEnum;
import cc.landingzone.dreamcmp.demo.workshop.service.StsService;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.credentials.models.CredentialModel;
import com.aliyun.oss.common.auth.Credentials;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentials;
import com.aliyun.sts20150401.models.AssumeRoleRequest;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
import com.aliyuncs.auth.BasicSessionCredentials;
import com.aliyuncs.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.auth.StaticCredentialsProvider;
import com.aliyuncs.profile.DefaultProfile;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author 恬裕
 * @date 2025/1/20
 */
@Configuration
public class FcClientConfig {

    @Autowired(required = false)
    private com.aliyun.credentials.Client credentialClient;

    @Value("${dreamcmp.workshop.assume_role_arn}")
    private String fcAccountRoleArn;

    @Autowired
    StsService stsService;

    @Bean
    @Profile("!dev")
    com.aliyun.fc20230330.Client fcClientEcsRole() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setCredential(credentialClient)
            .setEndpoint(EndpointEnum.FC.getEndpoint());
        return new com.aliyun.fc20230330.Client(config);
    }

    @Bean
    @Profile("dev")
    com.aliyun.fc20230330.Client fcClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(CommonConstants.Aliyun_AccessKeyId)
            .setAccessKeySecret(CommonConstants.Aliyun_AccessKeySecret)
            .setEndpoint(EndpointEnum.FC.getEndpoint());
        return new com.aliyun.fc20230330.Client(config);
    }

    @Bean
    @Profile("!dev")
    com.aliyun.sdk.service.fc20230330.AsyncClient asyncFcClient() {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
            .accessKeyId(CommonConstants.Aliyun_AccessKeyId)
            .accessKeySecret(CommonConstants.Aliyun_AccessKeySecret)
            .build());
        return com.aliyun.sdk.service.fc20230330.AsyncClient.builder()
            .region(CommonConstants.Aliyun_REGION_HANGZHOU)
            .credentialsProvider(provider)
            .overrideConfiguration(
                ClientOverrideConfiguration.create()
                    .setEndpointOverride(EndpointEnum.FC.getEndpoint())
            )
            .build();
    }

    @Bean
    @Profile("dev")
    com.aliyun.sdk.service.fc20230330.AsyncClient asyncFcClientTest() {
        AssumeRoleResponse crystalRole;
        try {
            crystalRole = stsService.assumeRole(new AssumeRoleRequest() {{
                setRoleArn(fcAccountRoleArn);
                setRoleSessionName("dreamcmp");
            }});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = crystalRole.getBody().getCredentials();
        String ak = credentials.getAccessKeyId();
        String sk = credentials.getAccessKeySecret();
        String token = credentials.getSecurityToken();
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
            .accessKeyId(ak)
            .accessKeySecret(sk)
            .securityToken(token)
            .build());
        return com.aliyun.sdk.service.fc20230330.AsyncClient.builder()
            .region(CommonConstants.Aliyun_REGION_HANGZHOU)
            .credentialsProvider(provider)
            .overrideConfiguration(
                ClientOverrideConfiguration.create()
                    .setEndpointOverride(EndpointEnum.FC.getEndpoint())
            )
            .build();
    }
}
