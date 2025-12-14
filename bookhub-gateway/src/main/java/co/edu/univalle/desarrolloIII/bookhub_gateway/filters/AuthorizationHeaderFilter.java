package co.edu.univalle.desarrolloIII.bookhub_gateway.filters;

import co.edu.univalle.desarrolloIII.bookhub_gateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Definir Rutas Públicas (no requieren token)
            Predicate<ServerHttpRequest> isApiSecured = r ->
                    !r.getURI().getPath().contains("/usuarios/login") &&
                            !r.getURI().getPath().contains("/usuarios/create");

            if (!isApiSecured.test(request)) {
                return chain.filter(exchange);
            }

            // 2. Verificar cabecera de Autorización
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return this.onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            // Extraer y validar el token
            final String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);
            final String token = authHeader.replace("Bearer ", "");

            try {
                // 3. Validar el Token y extraer información
                jwtUtil.validateToken(token);
                Claims claims = jwtUtil.getAllClaimsFromToken(token);

                // 4. Inyectar el Rol/ID en el encabezado para los microservicios
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-Auth-User-Role", claims.get("rol", String.class))
                        .header("X-Auth-User-Email", claims.getSubject())
                        .build();

                // Continuar con la cadena del filtro
                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
                // Token inválido o expirado
                return this.onError(exchange, "Authorization header is invalid or expired: " + e.getMessage(), HttpStatus.FORBIDDEN);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        return exchange.getResponse().setComplete();
    }
}