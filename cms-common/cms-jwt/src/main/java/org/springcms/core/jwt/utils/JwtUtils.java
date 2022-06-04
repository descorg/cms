package org.springcms.core.jwt.utils;

import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springcms.core.jwt.constant.TokenConstant;
import org.springcms.core.jwt.properties.JwtProperties;
import org.springcms.core.jwt.vo.CmsUser;
import org.springframework.data.redis.core.RedisTemplate;

public class JwtUtils {

    /**
     * 有效期30分钟
     */
    public static Integer TOKEN_EXPIRE = 30 * 60 * 1000;

    private static final String TOKEN_CACHE = "cmsx:token";
    private static final String TOKEN_LIST = "cmsx:token:";

    private static JwtProperties jwtProperties;

    private static RedisTemplate<String, Object> redisTemplate;

    public static JwtProperties getJwtProperties() {
        return jwtProperties;
    }

    public static void setJwtProperties(JwtProperties properties) {
        if (jwtProperties == null) {
            jwtProperties = properties;
        }
    }

    public static RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        if (JwtUtils.redisTemplate == null) {
            JwtUtils.redisTemplate = redisTemplate;
        }
    }

    /**
     * 生成token
     * @param uid
     * @return
     */
    public static String createToken(Integer uid, String name) {
        Map<String, Object> claims = new HashMap();
        claims.put(TokenConstant.USER_ID, uid);
        claims.put(TokenConstant.USER_NAME, name);

        return Jwts.builder()
                .setSubject("CmsX")
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRE))
                .signWith(SignatureAlgorithm.HS512, getJwtProperties().getSignKey().getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    public static Claims parseJWT(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(getJwtProperties().getSignKey().getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }

    public static String getAccessToken(String userId) {
        return String.valueOf(getRedisTemplate().opsForHash().get(TOKEN_CACHE, userId));
    }

    public static CmsUser getUser(String userId) {
        Object obj = getRedisTemplate().opsForValue().get(TOKEN_LIST.concat(userId));
        if (obj != null && obj instanceof CmsUser) {
            return (CmsUser) obj;
        }
        return null;
    }

    public static void addAccessToken(String userId, String accessToken, CmsUser user) {
        getRedisTemplate().opsForHash().put(TOKEN_CACHE, userId, accessToken);
        getRedisTemplate().opsForValue().set(TOKEN_LIST.concat(userId), user);
    }

    public static void removeAccessToken(String userId) {
        getRedisTemplate().opsForHash().delete(TOKEN_CACHE, userId);
        getRedisTemplate().delete(TOKEN_LIST.concat(userId));
    }
}
