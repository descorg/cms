package org.springcms.core.jwt.serializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.redis.serializer.RedisSerializer;

public class JwtRedisKeySerializer implements RedisSerializer<Object> {
    private final Charset charset;

    private final ConversionService converter;

    public JwtRedisKeySerializer() {
        this(StandardCharsets.UTF_8);
    }

    public JwtRedisKeySerializer(Charset charset) {
        Objects.requireNonNull(charset, "Charset must not be null");
        this.charset = charset;
        this.converter = DefaultConversionService.getSharedInstance();
    }

    public Object deserialize(byte[] bytes) {
        if (bytes == null)
            return null;
        return new String(bytes, this.charset);
    }

    public byte[] serialize(Object object) {
        String key;
        Objects.requireNonNull(object, "redis key is null");
        if (object instanceof org.springframework.cache.interceptor.SimpleKey) {
            key = "";
        } else if (object instanceof String) {
            key = (String)object;
        } else {
            key = (String)this.converter.convert(object, String.class);
        }
        return key.getBytes(this.charset);
    }
}
