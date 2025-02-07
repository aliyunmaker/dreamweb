package cc.landingzone.dreamcmp.common.config;

import cc.landingzone.dreamcmp.common.EndpointEnum;
import com.aliyun.credentials.models.CredentialModel;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.auth.DefaultCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Configuration
public class SlsClientConfig {

    @Autowired
    private com.aliyun.credentials.Client crossAccountCredentialClient;

    @Bean
    Client slsClient() {
        String endpoint = EndpointEnum.SLS.getEndpoint();
        return new Client(endpoint, () -> {
            CredentialModel credential = crossAccountCredentialClient.getCredential();
            String ak = credential.getAccessKeyId();
            String sk = credential.getAccessKeySecret();
            String token = credential.getSecurityToken();

            return new DefaultCredentials(ak, sk, token);
        });
    }
}
