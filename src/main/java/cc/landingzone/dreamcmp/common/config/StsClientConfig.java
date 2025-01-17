package cc.landingzone.dreamcmp.common.config;

import cc.landingzone.dreamcmp.common.EndpointEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import cc.landingzone.dreamcmp.common.CommonConstants;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Configuration
public class StsClientConfig {

    @Autowired(required = false)
    private com.aliyun.credentials.Client credentialClient;

    @Bean
    @Profile("!dev")
    com.aliyun.sts20150401.Client stsClientEcsRole() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setCredential(credentialClient)
            .setEndpoint(EndpointEnum.STS.getEndpoint());
        return new com.aliyun.sts20150401.Client(config);
    }

    @Bean
    com.aliyun.sts20150401.Client stsClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(CommonConstants.Aliyun_TestAccount_AccessKeyId)
            .setAccessKeySecret(CommonConstants.Aliyun_TestAccount_AccessKeySecret)
            .setSecurityToken(CommonConstants.Aliyun_TestAccount_SecurityToken)
            .setEndpoint(EndpointEnum.STS.getEndpoint());
        return new com.aliyun.sts20150401.Client(config);
    }
}

