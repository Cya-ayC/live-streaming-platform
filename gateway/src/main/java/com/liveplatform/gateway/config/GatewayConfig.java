package com.liveplatform.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Gateway配置类
 */
@Configuration
@Slf4j
public class GatewayConfig {

    /**
     * 自定义全局过滤器 - 添加请求/响应日志
     */
    @Bean
    public GlobalFilter customGlobalFilter() {
        return new GlobalFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                log.info("[Request] {} {} from {}",
                    exchange.getRequest().getMethod(),
                    exchange.getRequest().getPath(),
                    exchange.getRequest().getRemoteAddress());
                return chain.filter(exchange);
            }

            @Override
            public int getOrder() {
                return Ordered.LOWEST_PRECEDENCE;
            }
        };
    }

    /**
     * 配置Sentinel流控处理器
     */
    @Bean
    public BlockRequestHandler blockRequestHandler() {
        GatewayCallbackManager.setBlockHandler((exchange, t) -> {
            log.warn("[Sentinel] Request blocked: {}", t.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> response = new HashMap<>();
            response.put("code", 429);
            response.put("message", "Too many requests, please try again later");
            response.put("timestamp", System.currentTimeMillis());
            
            try {
                String json = new ObjectMapper().writeValueAsString(response);
                return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8)))
                );
            } catch (Exception e) {
                log.error("[Sentinel] Error writing response", e);
                return Mono.error(e);
            }
        });
        
        return new BlockRequestHandler() {
            @Override
            public Mono<Void> handleRequest(ServerWebExchange exchange, Throwable t) {
                return Mono.error(t);
            }
        };
    }
}
