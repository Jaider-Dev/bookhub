package co.edu.univalle.desarrolloIII.service_prestamos.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public Mono<ResponseEntity<Map<String, String>>> checkHealth() {
        return Mono.just(ResponseEntity.ok(Map.of("status", "UP", "service", "service-prestamos")));
    }
}
