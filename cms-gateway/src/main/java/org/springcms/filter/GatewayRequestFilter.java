package org.springcms.filter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(0)
public class GatewayRequestFilter implements GlobalFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("Content-Type", "application/json; charset=utf-8");

        String token = "";
        List<String> tokenHead = request.getHeaders().get("token");
        if (tokenHead != null) {
            token = tokenHead.get(0);
        }

        if (StringUtils.isEmpty(token)) {
            DataBuffer dataBuffer = createResponseBody(403, "Token is empty", response);
            return response.writeWith(Flux.just(dataBuffer));
        }

        return chain.filter(exchange);
    }

    private DataBuffer createResponseBody(int code, String message, ServerHttpResponse response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("message", message);
        ObjectMapper objectMapper = new ObjectMapper();
        String str = "";
        try {
            str = objectMapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            logger.error("json转换错误 {}", e.getLocalizedMessage());
        }
        DataBuffer dataBuffer = response.bufferFactory().wrap(str.getBytes());
        return dataBuffer;
    }
}
