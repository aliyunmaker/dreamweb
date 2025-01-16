package cc.landingzone.dreamcmp.common.config;


import cc.landingzone.dreamcmp.common.EndpointEnum;
import cc.landingzone.dreamcmp.demo.workshop.service.StsService;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.auth.Credentials;
import com.aliyun.openservices.log.common.auth.DefaultCredentials;
import com.aliyun.sts20150401.models.AssumeRoleRequest;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Configuration
public class SlsClientConfig {

    @Autowired
    private StsService stsService;

    @Value("${dreamcmp.workshop.assume_role_arn}")
    private String slsAccountRoleArn;

    @Bean
    @Profile("!dev")
    Client slsClientEcsRole() {
        String endpoint = EndpointEnum.SLS.getEndpoint();
        return new Client(endpoint, () -> {
            // 特殊场景（资源在crystal），需要跨账号扮演到crystal账号里
            AssumeRoleResponse crystalRole;
            try {
                crystalRole = stsService.assumeRole(new AssumeRoleRequest() {{
                    setRoleArn(slsAccountRoleArn);
                    setRoleSessionName("dreamcmp");
                }});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = crystalRole.getBody().getCredentials();
            String ak = credentials.getAccessKeyId();
            String sk = credentials.getAccessKeySecret();
            String token = credentials.getSecurityToken();

            return new DefaultCredentials(ak, sk, token);
        });
    }

    @Bean
    Client slsClient() {
        String endpoint = EndpointEnum.SLS.getEndpoint();
        // 特殊场景（资源在crystal），需要跨账号扮演到crystal账号里
        AssumeRoleResponse crystalRole;
        try {
            crystalRole = stsService.assumeRole(new AssumeRoleRequest() {{
                setRoleArn(slsAccountRoleArn);
                setRoleSessionName("dreamcmp");
            }});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials creds = crystalRole.getBody().getCredentials();

        Credentials credentials = new DefaultCredentials(
            creds.getAccessKeyId(),
            creds.getAccessKeySecret(),
            creds.getSecurityToken()
        );
        // 仅本地测试使用
        return new Client(endpoint, credentials, null);
    }
}
