package com.sky.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.json.JacksonObjectMapper;
import com.sky.result.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.time.Duration;

/**
 * @description: 此配置用于设置Spring Cache,
 * 包含设置缓存过期时间、缓存键的序列化方式、缓存值的序列化方式、设置缓存前缀、关闭缓存空值
 */
@Configuration
public class CacheConfig {
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisSerializer<Object> serializer = new RedisSerializer<>() {
            private final ObjectMapper mapper = new JacksonObjectMapper();

            @Override
            public byte[] serialize(Object value) throws SerializationException {
                if (value instanceof Result) {
                    // 序列化 Result 对象本身，而不是只序列化 data 字段
                    try {
                        return mapper.writeValueAsBytes(value);
                    } catch (JsonProcessingException e) {
                        throw new SerializationException("Error serializing value", e);
                    }
                }
                try {
                    return mapper.writeValueAsBytes(value);
                } catch (JsonProcessingException e) {
                    throw new SerializationException("Error serializing value", e);
                }
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                try {
                    // 指定反序列化为 Result 类型
                    return mapper.readValue(bytes, Result.class);
                } catch (IOException e) {
                    throw new SerializationException("Error deserializing value", e);
                }
            }
        };

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofDays(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .prefixCacheNameWith("sky-take-out:")
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
