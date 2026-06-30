package com.jarviswuod.improvedbillingsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

import static java.util.Map.entry;

@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory factory,
            @Value("${spring.cache.redis.key-prefix:billing:}") String keyPrefix
    ) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .prefixCacheNameWith(keyPrefix)
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );

        Map<String, RedisCacheConfiguration> perCache = Map.ofEntries(
                entry(CacheNames.CUSTOMERS, config.entryTtl(Duration.ofMinutes(10))),
                entry(CacheNames.CUSTOMER_DETAILS, config.entryTtl(Duration.ofMinutes(15))),
                entry(CacheNames.DELETED_CUSTOMERS, config.entryTtl(Duration.ofMinutes(5))),
                entry(CacheNames.INVOICES, config.entryTtl(Duration.ofMinutes(10))),
                entry(CacheNames.INVOICE_DETAILS, config.entryTtl(Duration.ofMinutes(15))),
                entry(CacheNames.OVERDUE_INVOICES, config.entryTtl(Duration.ofMinutes(5))),
                entry(CacheNames.PAYMENTS, config.entryTtl(Duration.ofMinutes(10))),
                entry(CacheNames.PAYMENT_DETAILS, config.entryTtl(Duration.ofMinutes(15))),
                entry(CacheNames.DASHBOARD_SUMMARY, config.entryTtl(Duration.ofMinutes(5))),
                entry(CacheNames.TOP_CUSTOMERS, config.entryTtl(Duration.ofMinutes(5))),
                entry(CacheNames.MONTHLY_REVENUE, config.entryTtl(Duration.ofMinutes(5)))
        );
        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(perCache)
                .build();
    }
}
