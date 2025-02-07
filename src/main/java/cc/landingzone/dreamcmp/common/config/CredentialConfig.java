package cc.landingzone.dreamcmp.common.config;

import cc.landingzone.dreamcmp.common.CommonConstants;
import com.aliyun.credentials.models.CredentialModel;
import com.aliyun.credentials.provider.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.credentials.Client;

/**
 * @author yicheng.fyc
 * @date 2025/1/14
 */
@Configuration
public class CredentialConfig {

    @Value("${spring.profiles.active}")
    private String env;

    @Value("${dreamcmp.workshop.ecs_instance_role}")
    private String ecsInstanceRole;

    @Value("${dreamcmp.workshop.assume_role_arn}")
    private String assumeRoleArn;

    private AlibabaCloudCredentialsProvider getOriginalCredentialProvider() {
        AlibabaCloudCredentialsProvider originalProvider;

        if ("dev".equals(env)) {
            // 1. 本地测试使用
            CredentialModel credential = CredentialModel.builder()
                .accessKeyId(CommonConstants.Aliyun_TestAccount_AccessKeyId)
                .accessKeySecret(CommonConstants.Aliyun_TestAccount_AccessKeySecret)
                .securityToken(CommonConstants.Aliyun_TestAccount_SecurityToken)
                .build();
            originalProvider = StaticCredentialsProvider.builder()
                .credential(credential)
                .build();
        } else {
            // 2. 线上使用ECS实例RAM角色
            originalProvider = EcsRamRoleCredentialProvider.builder()
                .roleName(ecsInstanceRole)
                .disableIMDSv1(true)
                .build();
        }

        return originalProvider;
    }

    @Bean
    Client originalCredentialClient() {
        AlibabaCloudCredentialsProvider originalProvider = getOriginalCredentialProvider();
        return new Client(originalProvider);
    }

    // 通过RAM Role Provider跨账号获取STS Token
    @Bean
    Client crossAccountCredentialClient() {
        AlibabaCloudCredentialsProvider originalProvider = getOriginalCredentialProvider();

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
