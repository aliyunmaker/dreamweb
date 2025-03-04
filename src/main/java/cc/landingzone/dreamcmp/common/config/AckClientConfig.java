package cc.landingzone.dreamcmp.common.config;

import com.aliyun.cs20151215.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Author: laodouza
 * Date: 2025/2/21
 */
@Configuration
public class AckClientConfig {

    @Autowired
    private com.aliyun.credentials.Client crossAccountCredentialClient;

    @Bean
    Client ackClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setCredential(crossAccountCredentialClient)
            .setEndpoint("cs.cn-hangzhou.aliyuncs.com");
        return new Client(config);
    }
}
