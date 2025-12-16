package co.edu.univalle.desarrolloIII.service_prestamos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @org.springframework.beans.factory.annotation.Value("${gateway.url:http://127.0.0.1:8080}")
    private String gatewayUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(gatewayUrl)
                .build();
    }
}