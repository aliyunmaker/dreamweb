package cc.landingzone.dreamcmp.common.config;


import cc.landingzone.dreamcmp.common.CommonConstants;
import cc.landingzone.dreamcmp.common.EndpointEnum;
import com.aliyun.credentials.models.CredentialModel;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.auth.Credentials;
import com.aliyun.openservices.log.common.auth.DefaultCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Configuration
public class SlsClientConfig {

    @Autowired(required = false)
    private com.aliyun.credentials.Client credentialClient;

    @Bean
    @Profile("!dev")
    Client slsClientEcsRole() {
        String endpoint = EndpointEnum.SLS.getEndpoint();
        return new Client(endpoint, () -> {
            // 保证线程安全，从 CredentialModel 中获取 ak/sk/security token
            CredentialModel credentialModel = credentialClient.getCredential();
            String ak = credentialModel.getAccessKeyId();
            String sk = credentialModel.getAccessKeySecret();
            String token = credentialModel.getSecurityToken();
            return new DefaultCredentials(ak, sk, token);
        });
    }

    @Bean
    Client slsClient() {
        String endpoint = EndpointEnum.SLS.getEndpoint();
        Credentials credentials = new DefaultCredentials(
            CommonConstants.Aliyun_TestAccount_AccessKeyId,
            CommonConstants.Aliyun_TestAccount_AccessKeySecret,
            CommonConstants.Aliyun_TestAccount_SecurityToken
        );
        return new Client(endpoint, credentials, null);
    }
}
