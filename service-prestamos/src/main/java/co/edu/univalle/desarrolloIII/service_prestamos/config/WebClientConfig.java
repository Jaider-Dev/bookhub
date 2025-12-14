package co.edu.univalle.desarrolloIII.service_prestamos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final String GATEWAY_BASE_URL = "http://127.0.0.1:8080";

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(GATEWAY_BASE_URL)
                .build();
    }
}