package co.edu.univalle.desarrolloIII.service_inventario.controller;

import co.edu.univalle.desarrolloIII.service_inventario.enums.EstadoEjemplar;
import co.edu.univalle.desarrolloIII.service_inventario.model.Ejemplar;
import co.edu.univalle.desarrolloIII.service_inventario.service.EjemplarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/ejemplares")
public class EjemplarController {

    @Autowired
    private EjemplarService ejemplarService;

    @GetMapping("/{id}")
    public ResponseEntity<Ejemplar> getEjemplarById(@PathVariable Long id) {
        return ejemplarService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public java.util.List<Ejemplar> getAllEjemplares() {
        return ejemplarService.findAll();
    }

    @PostMapping
    public ResponseEntity<Ejemplar> createEjemplar(@RequestBody Ejemplar ejemplar) {
        return ResponseEntity.ok(ejemplarService.save(ejemplar));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> updateEjemplarEstado(@PathVariable Long id,
                                                  @RequestParam EstadoEjemplar nuevoEstado) {
        Optional<Ejemplar> updatedEjemplar = ejemplarService.updateEstado(id, nuevoEstado);

        if (updatedEjemplar.isPresent()) {
            return ResponseEntity.ok(updatedEjemplar.get());
        }
        return ResponseEntity.notFound().build();
    }
}