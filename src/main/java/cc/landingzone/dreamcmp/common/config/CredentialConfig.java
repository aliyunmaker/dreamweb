package cc.landingzone.dreamcmp.common.config;

import com.aliyun.credentials.provider.EcsRamRoleCredentialProvider;
import com.aliyun.credentials.provider.RamRoleArnCredentialProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.aliyun.credentials.Client;
import com.aliyun.credentials.models.Config;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Configuration
public class CredentialConfig {

    @Value("${dreamcmp.workshop.ecs_instance_role}")
    private String ecsInstanceRole;

    @Value("${dreamcmp.workshop.assume_role_arn}")
    private String assumeRoleArn;

    // 通过RAM Role Provider跨账号获取STS Token
    @Bean(name = "credentialClient")
    @Profile("!dev")
    Client getCredentialClient() {
        // 1. 使用ECS实例RAM角色
        EcsRamRoleCredentialProvider originalProvider = EcsRamRoleCredentialProvider.builder()
            .roleName(ecsInstanceRole)
            .disableIMDSv1(true)
            .build();

        // 使用当前角色作为入参，初始化角色扮演的Provider，实现角色链式扮演，同时支持跨账号扮演角色
        RamRoleArnCredentialProvider provider = RamRoleArnCredentialProvider.builder()
            .credentialsProvider(originalProvider)
            .durationSeconds(3600)
            .roleArn(assumeRoleArn)
            .roleSessionName("dreamcmp")
            .build();

        return new Client(provider);
    }
}
