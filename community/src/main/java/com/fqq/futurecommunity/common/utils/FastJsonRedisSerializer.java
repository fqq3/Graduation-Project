package com.fqq.futurecommunity.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * @author: fqq
 * @Date: 2026/4/9 - 04 - 09 - 21:50
 * @Description: com.fqq.futurecommunity.common.utils
 * @version: 1.0
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(T t) throws SerializationException {
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (Exception e) {
            throw new SerializationException("序列化失败", e);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        try {
            if (bytes == null) return null;
            return (T) objectMapper.readValue(bytes, Object.class);
        } catch (Exception e) {
            throw new SerializationException("反序列化失败", e);
        }
    }
}
