package cc.landingzone.dreamcmp.common.config;

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

    // 初始化凭据客户端
    // 您可以在代码中显式配置，来初始化凭据客户端
    // 荐您使用该方式，在代码中明确指定实例角色，避免运行环境中的环境变量、配置文件等带来非预期的结果。
    @Bean(name = "credentialClient")
    @Profile("!dev")
    Client getCredentialClient() {
       Config config = new Config()
           .setType("ecs_ram_role")
           // 选填，该ECS实例角色的角色名称，不填会自动获取，建议加上以减少请求次数
        //    .setRoleName("<请填写ECS实例角色的角色名称>")
           // 在加固模式下获取STS Token，强烈建议开启
           .setEnableIMDSv2(true);
       return new Client(config);
    }
}
