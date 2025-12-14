package co.edu.univalle.desarrolloIII.service_prestamos.controller;

import co.edu.univalle.desarrolloIII.service_prestamos.model.Prestamo;
import co.edu.univalle.desarrolloIII.service_prestamos.service.PrestamoService;
import jakarta.validation.Valid; // <--- Import necesario para @Valid (si usas Spring Boot 3+)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @PostMapping
    public ResponseEntity<Prestamo> crearPrestamo(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody Prestamo prestamo) {

        Prestamo nuevoPrestamo = prestamoService.createPrestamo(prestamo, authHeader);

        return new ResponseEntity<>(nuevoPrestamo, HttpStatus.CREATED);
    }
}