package co.edu.univalle.desarrolloIII.bookhub_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

// DISABLED: CORS is now handled via Spring Cloud Gateway built-in globalcors configuration
// in application.properties to avoid duplicate CORS headers
//@Configuration
public class CorsConfig {

    // @Bean
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
