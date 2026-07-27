package com.liveplatform.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final String SECRET_KEY = "your-secret-key-change-it-in-production";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 获取请求头中的Authorization
            String token = getTokenFromRequest(exchange);

            // 如果是公开接口，直接放行
            if (isPublicEndpoint(exchange.getRequest().getPath().toString())) {
                return chain.filter(exchange);
            }

            // 验证Token
            if (token == null || token.isEmpty()) {
                log.warn("[Auth] Missing token");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().writeWith(null);
            }

            try {
                Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token)
                    .getBody();

                // 将用户信息添加到请求头
                exchange.getRequest().mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Name", (String) claims.get("username"))
                    .build();

                log.debug("[Auth] Token verified for user: {}", claims.getSubject());
                return chain.filter(exchange);
            } catch (SignatureException e) {
                log.warn("[Auth] Invalid token signature", e);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().writeWith(null);
            } catch (Exception e) {
                log.warn("[Auth] Token validation failed", e);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().writeWith(null);
            }
        };
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 判断是否是公开接口
     */
    private boolean isPublicEndpoint(String path) {
        return path.contains("/user/login") ||
            path.contains("/user/register") ||
            path.contains("/live/rooms/list") ||
            path.contains("/health");
    }

    public static class Config {
        // 配置类
    }
}
