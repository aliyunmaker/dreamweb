package cc.landingzone.dreamcmp.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Configuration
public class StsClientConfig {

    @Autowired
    private com.aliyun.credentials.Client originalCredentialClient;

    @Bean
    com.aliyun.sts20150401.Client stsClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setCredential(originalCredentialClient)
            // 以华东1（杭州）为例
            .setEndpoint("sts.cn-hangzhou.aliyuncs.com");
        return new com.aliyun.sts20150401.Client(config);
    }
}

