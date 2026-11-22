package com.fqq.futurecommunity.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author: fqq
 * @Date: 2026/4/9 - 04 - 09 - 22:01
 * @Description: com.fqq.futurecommunity.framework.config
 * @version: 1.0
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // key 序列化
        template.setKeySerializer(new StringRedisSerializer());

        // value 序列化（用 Jackson）
        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(Object.class);

        template.setValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}