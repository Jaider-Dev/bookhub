package co.edu.univalle.desarrolloIII.service_prestamos.controller;

import co.edu.univalle.desarrolloIII.service_prestamos.model.Prestamo;
import co.edu.univalle.desarrolloIII.service_prestamos.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono; // Importar Mono

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @PostMapping
    public Mono<Prestamo> crearPrestamo(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody @Valid Prestamo prestamo) {

        return prestamoService.createPrestamo(prestamo, authHeader);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Prestamo>> getPrestamoById(@PathVariable Long id) {

        return prestamoService.obtenerPrestamoPorId(id)
                .map(optionalPrestamo -> optionalPrestamo
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build())
                );
    }
}