package com.jarviswuod.improvedbillingsystem.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(mapper);

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
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
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
