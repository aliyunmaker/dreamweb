package cc.landingzone.dreamcmp.common.config;

import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.EndpointEnum;
import cc.landingzone.dreamcmp.demo.workshop.service.StsService;
import com.aliyun.auth.credentials.provider.RamRoleArnCredentialProvider;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    RamRoleArnCredentialProvider ramRoleArnCredentialProvider;

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
    com.aliyun.sdk.service.fc20230330.AsyncClient asyncFcClientTestWithoutAK() {
        return com.aliyun.sdk.service.fc20230330.AsyncClient.builder()
            .region(CommonConstants.Aliyun_REGION_HANGZHOU)
            .credentialsProvider(ramRoleArnCredentialProvider)
            .overrideConfiguration(
                ClientOverrideConfiguration.create()
                    .setEndpointOverride(EndpointEnum.FC.getEndpoint())
            )
            .build();
    }

    //@Bean
    //com.aliyun.sdk.service.fc20230330.AsyncClient asyncFcClientWithAK() {
    //    StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
    //        .accessKeyId(CommonConstants.Aliyun_AccessKeyId)
    //        .accessKeySecret(CommonConstants.Aliyun_AccessKeySecret)
    //        .build());
    //    return com.aliyun.sdk.service.fc20230330.AsyncClient.builder()
    //        .region(CommonConstants.Aliyun_REGION_HANGZHOU)
    //        .credentialsProvider(provider)
    //        .overrideConfiguration(
    //            ClientOverrideConfiguration.create()
    //                .setEndpointOverride(EndpointEnum.FC.getEndpoint())
    //        )
    //        .build();
    //}
}
