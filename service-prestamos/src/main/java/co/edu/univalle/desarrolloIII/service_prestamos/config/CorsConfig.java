package co.edu.univalle.desarrolloIII.service_prestamos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

// ⚠️ CORS DESACTIVADO AQUÍ - El Gateway maneja CORS para todas las peticiones
// Las peticiones del cliente van al Gateway (puerto 8080) que aplica CORS globalmente.
// Este microservicio está detrás del Gateway, por eso no necesita CORS propio.
/*
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // Permitir solicitudes desde el frontend
        corsConfig.addAllowedOrigin("http://localhost:4200");
        corsConfig.addAllowedOrigin("http://localhost:3000");
        
        // Permitir todos los métodos HTTP
        corsConfig.addAllowedMethod("*");
        
        // Permitir todos los headers
        corsConfig.addAllowedHeader("*");
        
        // Permitir credenciales (cookies, autorización)
        corsConfig.setAllowCredentials(true);
        
        // Permitir headers específicos importantes
        corsConfig.addExposedHeader("Authorization");
        corsConfig.addExposedHeader("Content-Type");
        corsConfig.addExposedHeader("X-Requested-With");
        
        // Tiempo de caché para preflight
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}
*/

public class CorsConfig {
    // CORS configuration is handled by the Gateway (port 8080)
    // This microservice doesn't need CORS configuration
}
