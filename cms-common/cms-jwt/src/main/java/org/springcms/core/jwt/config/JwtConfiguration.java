package org.springcms.core.jwt.config;

import org.springcms.core.jwt.properties.JwtProperties;
import org.springcms.core.jwt.serializer.JwtRedisKeySerializer;
import org.springcms.core.jwt.utils.JwtUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({JwtRedisConfiguration.class})
@EnableConfigurationProperties({JwtProperties.class})
public class JwtConfiguration implements SmartInitializingSingleton {
    private final JwtProperties jwtProperties;

    private final RedisConnectionFactory redisConnectionFactory;

    public JwtConfiguration(JwtProperties jwtProperties, RedisConnectionFactory redisConnectionFactory) {
        this.jwtProperties = jwtProperties;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public void afterSingletonsInstantiated() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
        JwtRedisKeySerializer redisKeySerializer = new JwtRedisKeySerializer();
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        redisTemplate.setKeySerializer((RedisSerializer)redisKeySerializer);
        redisTemplate.setHashKeySerializer((RedisSerializer)redisKeySerializer);
        redisTemplate.setValueSerializer((RedisSerializer)jdkSerializationRedisSerializer);
        redisTemplate.setHashValueSerializer((RedisSerializer)jdkSerializationRedisSerializer);
        redisTemplate.setConnectionFactory(this.redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        JwtUtils.setJwtProperties(this.jwtProperties);
        JwtUtils.setRedisTemplate(redisTemplate);
    }
}
