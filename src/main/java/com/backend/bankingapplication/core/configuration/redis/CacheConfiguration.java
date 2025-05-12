package com.backend.bankingapplication.core.configuration.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

import static com.backend.bankingapplication.core.constants.KeyPrefix.USER_PROFILES_PREFIX;

@Configuration
public class CacheConfiguration {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.builder(factory)
                .withCacheConfiguration(USER_PROFILES_PREFIX, RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(30)))
                .build();
    }
}
