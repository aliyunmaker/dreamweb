//package cc.landingzone.dreamweb.framework;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.integration.redis.util.RedisLockRegistry;
//
//@Configuration
//public class RedisLockConfiguration {
//    private static final long expireAfter = 60000L;
//
//    @Bean
//    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
//        return new RedisLockRegistry(redisConnectionFactory, "dreamweb", expireAfter);
//    }
//}
