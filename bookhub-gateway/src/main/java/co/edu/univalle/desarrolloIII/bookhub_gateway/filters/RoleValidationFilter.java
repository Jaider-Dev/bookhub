package co.edu.univalle.desarrolloIII.bookhub_gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("RoleValidationFilter")
public class RoleValidationFilter extends AbstractGatewayFilterFactory<RoleValidationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(RoleValidationFilter.class);

    private final AuthSecurityProperties authSecurityProperties;

    public static class Config {
    }

    public RoleValidationFilter(AuthSecurityProperties authSecurityProperties) {
        super(Config.class);
        this.authSecurityProperties = authSecurityProperties;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            String method = request.getMethod().name();

            String userRole = request.getHeaders().getFirst("X-Auth-User-Role");

            boolean requiresAdmin = authSecurityProperties.getAdminPaths().stream()
                    .anyMatch(securityPath -> {
                        boolean pathMatches;
                        String configPath = securityPath.getPath();

                        if (configPath.endsWith("/**")) {
                            String base = configPath.replace("/**", "");
                            pathMatches = path.startsWith(base);
                        }
                        else if (configPath.contains("*")) {
                            String regexPath = configPath.replace("/", "\\/").replace("*", "[^\\/]+");
                            pathMatches = path.matches(regexPath);
                        }
                        else {
                            pathMatches = path.equals(configPath);
                        }

                        boolean methodMatches = securityPath.getMethods().isEmpty() || securityPath.getMethods().contains(method);

                        return pathMatches && methodMatches;
                    });

            if (requiresAdmin) {
                if (userRole == null || !userRole.equals("ADMIN")) {
                    return this.onError(exchange, "Acceso denegado. Se requiere el rol ADMIN.", HttpStatus.FORBIDDEN);
                }
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        return exchange.getResponse().setComplete();
    }
}