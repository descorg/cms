package org.springcms.jwt.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.springcms.jwt.properties.JwtProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

public class JwtUtils {
    public static String BEARER = "bearer";

    public static Integer AUTH_LENGTH = Integer.valueOf(7);

    private static final String REFRESH_TOKEN_CACHE = "cmsx:refreshToken";

    private static final String TOKEN_CACHE = "cmsx:token";

    private static final String TOKEN_KEY = "token:state:";

    private static JwtProperties jwtProperties;

    private static RedisTemplate<String, Object> redisTemplate;

    public static JwtProperties getJwtProperties() {
        return jwtProperties;
    }

    public static void setJwtProperties(JwtProperties properties) {
        if (jwtProperties == null)
            jwtProperties = properties;
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        if (JwtUtils.redisTemplate == null)
            JwtUtils.redisTemplate = redisTemplate;
    }

    public static String getBase64Security() {
        return Base64.getEncoder().encodeToString(getJwtProperties().getSignKey().getBytes(StandardCharsets.UTF_8));
    }

    public static String getToken(String auth) {
        if (auth != null && auth.length() > AUTH_LENGTH.intValue()) {
            String headStr = auth.substring(0, 6).toLowerCase();
            if (headStr.compareTo(BEARER) == 0)
                auth = auth.substring(7);
            return auth;
        }
        return null;
    }

    public static Claims parseJWT(String jsonWebToken) {
        try {
            return (Claims)Jwts.parserBuilder()
                    .setSigningKey(Base64.getDecoder().decode(getBase64Security())).build()
                    .parseClaimsJws(jsonWebToken).getBody();
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getAccessToken(String userId, String accessToken) {
        return String.valueOf(getRedisTemplate().opsForValue().get(getAccessTokenKey(userId, accessToken)));
    }

    public static void addAccessToken(String userId, String accessToken, int expire) {
        getRedisTemplate().delete(getAccessTokenKey(userId, accessToken));
        getRedisTemplate().opsForValue().set(getAccessTokenKey(userId, accessToken), accessToken, expire, TimeUnit.SECONDS);
    }

    public static void removeAccessToken(String userId) {
        removeAccessToken(userId, null);
    }

    public static void removeAccessToken(String userId, String accessToken) {
        getRedisTemplate().delete(getAccessTokenKey(userId, accessToken));
    }

    public static String getAccessTokenKey(String userId, String accessToken) {
        String key = TOKEN_CACHE.concat("::").concat(TOKEN_KEY);
        if (getJwtProperties().getSingle().booleanValue() || StringUtils.isEmpty(accessToken))
            return key.concat(userId);
        return key.concat(accessToken);
    }

    public static String getRefreshToken(String userId, String refreshToken) {
        return String.valueOf(getRedisTemplate().opsForValue().get(getRefreshTokenKey(userId)));
    }

    public static void addRefreshToken(String userId, String refreshToken, int expire) {
        getRedisTemplate().delete(getRefreshTokenKey(userId));
        getRedisTemplate().opsForValue().set(getRefreshTokenKey(userId), refreshToken, expire, TimeUnit.SECONDS);
    }

    public static void removeRefreshToken(String userId) {
        getRedisTemplate().delete(getRefreshTokenKey(userId));
    }

    public static String getRefreshTokenKey(String userId) {
        return REFRESH_TOKEN_CACHE.concat("::").concat(TOKEN_KEY).concat(userId);
    }
}
