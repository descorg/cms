package org.springcms.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springcms.jwt.constant.TokenConstant;
import org.springcms.properties.AuthProperties;
import org.springcms.jwt.properties.JwtProperties;
import org.springcms.jwt.utils.JwtUtils;
import org.springcms.provider.AuthProvider;
import org.springcms.provider.RequestProvider;
import org.springcms.provider.ResponseProvider;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 鉴权认证
 *
 * @author Chill
 */
@Slf4j
@Component
@AllArgsConstructor
public class AuthFilter implements GlobalFilter, Ordered {
    private final AuthProperties authProperties;
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //校验 Token 放行
        String originalRequestUrl = RequestProvider.getOriginalRequestUrl(exchange);
        String path = exchange.getRequest().getURI().getPath();
        if (isSkip(path) || isSkip(originalRequestUrl)) {
            return chain.filter(exchange);
        }
        //校验 Token 合法性
        ServerHttpResponse resp = exchange.getResponse();
        String headerToken = exchange.getRequest().getHeaders().getFirst(AuthProvider.AUTH_KEY);
        String paramToken = exchange.getRequest().getQueryParams().getFirst(AuthProvider.AUTH_KEY);
        if (StringUtils.isBlank(headerToken) && StringUtils.isBlank(paramToken)) {
            return unAuth(resp, "缺失令牌,鉴权失败");
        }
        String auth = StringUtils.isBlank(headerToken) ? paramToken : headerToken;
        String token = JwtUtils.getToken(auth);
        Claims claims = JwtUtils.parseJWT(token);
        if (token == null || claims == null) {
            return unAuth(resp, "请求未授权");
        }
        //判断 Token 状态
        if (jwtProperties.getState()) {
            String tenantId = String.valueOf(claims.get(TokenConstant.TENANT_ID));
            String userId = String.valueOf(claims.get(TokenConstant.USER_ID));
            String accessToken = JwtUtils.getAccessToken(tenantId, userId, token);
            if (!token.equalsIgnoreCase(accessToken)) {
                return unAuth(resp, "令牌已失效");
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isSkip(String path) {
        return AuthProvider.getDefaultSkipUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path))
                || authProperties.getSkipUrl().stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private Mono<Void> unAuth(ServerHttpResponse resp, String msg) {
        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        String result = "";
        try {
            result = objectMapper.writeValueAsString(ResponseProvider.unAuth(msg));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        DataBuffer buffer = resp.bufferFactory().wrap(result.getBytes(StandardCharsets.UTF_8));
        return resp.writeWith(Flux.just(buffer));
    }
}
