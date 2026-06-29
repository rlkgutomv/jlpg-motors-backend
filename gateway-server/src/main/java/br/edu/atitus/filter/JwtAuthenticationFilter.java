package br.edu.atitus.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import javax.crypto.SecretKey;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final String SECRET_KEY = "minha_chave_secreta_muito_segura_e_longa_para_o_jwt_jlpg_motors";

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Header de autorização ausente", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Padrão Token inválido", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                HttpMethod method = request.getMethod();
                String path = request.getURI().getPath();
                String role = claims.get("role", String.class);

                if (role == null) {
                    return onError(exchange, "Acesso negado: Nenhuma role encontrada no token", HttpStatus.FORBIDDEN);
                }

                String userRole = role.toUpperCase();

                if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE) {

                    if (path.contains("/favorites") || (path.contains("/negotiations") && !path.contains("/admin"))) {
                        if (!userRole.contains("ADMIN") && !userRole.contains("USER")) {
                            return onError(exchange, "Acesso negado: Permissão insuficiente para esta ação", HttpStatus.FORBIDDEN);
                        }
                    }

                    else {
                        if (!userRole.contains("ADMIN")) {
                            return onError(exchange, "Acesso negado: Apenas administradores podem gerenciar veículos", HttpStatus.FORBIDDEN);
                        }
                    }
                }

            } catch (Exception e) {
                return onError(exchange, "Token JWT inválido ou expirado", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("X-Auth-Error", err);
        return response.setComplete();
    }

    public static class Config {}
}