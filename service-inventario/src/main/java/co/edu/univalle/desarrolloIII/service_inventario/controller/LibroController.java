package co.edu.univalle.desarrolloIII.service_inventario.controller;

import co.edu.univalle.desarrolloIII.service_inventario.model.Libro;
import co.edu.univalle.desarrolloIII.service_inventario.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping
    public List<?> getAllLibros(@RequestParam(required = false) Boolean withDetails) {
        if (Boolean.TRUE.equals(withDetails)) {
            return libroService.findAllWithDetails();
        }
        return libroService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> getLibroById(@PathVariable Long id) {
        return libroService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Libro createLibro(@RequestBody Map<String, Object> libroData) {
        // Manejar tanto el formato antiguo (con autorId/categoriaId) como el nuevo (con autor/categoria como strings)
        String titulo = (String) libroData.get("titulo");
        String isbn = libroData.get("isbn") != null ? libroData.get("isbn").toString() : null;
        Integer anioPublicacion = libroData.get("anioPublicacion") != null ? 
            Integer.parseInt(libroData.get("anioPublicacion").toString()) : null;
        
        String autorNombre = null;
        String categoriaNombre = null;
        Integer ejemplares = null;
        
        // Si viene como string (nuevo formato)
        if (libroData.containsKey("autor") && libroData.get("autor") instanceof String) {
            autorNombre = (String) libroData.get("autor");
        }
        if (libroData.containsKey("categoria") && libroData.get("categoria") instanceof String) {
            categoriaNombre = (String) libroData.get("categoria");
        }
        if (libroData.containsKey("ejemplares")) {
            Object ejemplaresObj = libroData.get("ejemplares");
            if (ejemplaresObj instanceof Number) {
                ejemplares = ((Number) ejemplaresObj).intValue();
            } else if (ejemplaresObj instanceof String) {
                try {
                    ejemplares = Integer.parseInt((String) ejemplaresObj);
                } catch (NumberFormatException e) {
                    ejemplares = 0;
                }
            }
        }
        
        // Si tenemos nombres de autor y categoría, usar el método nuevo
        if (autorNombre != null && categoriaNombre != null) {
            return libroService.saveWithAutorAndCategoria(titulo, isbn, anioPublicacion, 
                                                          autorNombre, categoriaNombre, ejemplares);
        }
        
        // Si no, usar el método antiguo (compatibilidad hacia atrás)
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setIsbn(isbn);
        libro.setAnioPublicacion(anioPublicacion);
        if (libroData.containsKey("autorId")) {
            libro.setAutorId(Long.parseLong(libroData.get("autorId").toString()));
        }
        if (libroData.containsKey("categoriaId")) {
            libro.setCategoriaId(Long.parseLong(libroData.get("categoriaId").toString()));
        }
        return libroService.save(libro);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> updateLibro(@PathVariable Long id, @RequestBody Map<String, Object> libroData) {
        return libroService.findById(id)
                .map(libroExistente -> {
                    if (libroData.containsKey("titulo")) {
                        libroExistente.setTitulo((String) libroData.get("titulo"));
                    }
                    if (libroData.containsKey("isbn")) {
                        libroExistente.setIsbn(libroData.get("isbn").toString());
                    }
                    if (libroData.containsKey("anioPublicacion")) {
                        libroExistente.setAnioPublicacion(Integer.parseInt(libroData.get("anioPublicacion").toString()));
                    }
                    if (libroData.containsKey("autorId")) {
                        libroExistente.setAutorId(Long.parseLong(libroData.get("autorId").toString()));
                    }
                    if (libroData.containsKey("categoriaId")) {
                        libroExistente.setCategoriaId(Long.parseLong(libroData.get("categoriaId").toString()));
                    }
                    return ResponseEntity.ok(libroService.save(libroExistente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibro(@PathVariable Long id) {
        if (libroService.findById(id).isPresent()) {
            libroService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}