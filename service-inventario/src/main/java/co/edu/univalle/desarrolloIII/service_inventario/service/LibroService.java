package co.edu.univalle.desarrolloIII.service_inventario.service;

import co.edu.univalle.desarrolloIII.service_inventario.model.Autor;
import co.edu.univalle.desarrolloIII.service_inventario.model.Categoria;
import co.edu.univalle.desarrolloIII.service_inventario.model.Ejemplar;
import co.edu.univalle.desarrolloIII.service_inventario.model.Libro;
import co.edu.univalle.desarrolloIII.service_inventario.repository.AutorRepository;
import co.edu.univalle.desarrolloIII.service_inventario.repository.CategoriaRepository;
import co.edu.univalle.desarrolloIII.service_inventario.repository.EjemplarRepository;
import co.edu.univalle.desarrolloIII.service_inventario.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EjemplarRepository ejemplarRepository;

    public List<Libro> findAll() {
        return libroRepository.findAll();
    }

    public List<co.edu.univalle.desarrolloIII.service_inventario.dto.LibroResponseDto> findAllWithDetails() {
        List<Libro> libros = libroRepository.findAll();
        List<Autor> autores = autorRepository.findAll();
        List<Categoria> categorias = categoriaRepository.findAll();
        List<Ejemplar> ejemplares = ejemplarRepository.findAll();

        return libros.stream().map(libro -> {
            co.edu.univalle.desarrolloIII.service_inventario.dto.LibroResponseDto dto = 
                new co.edu.univalle.desarrolloIII.service_inventario.dto.LibroResponseDto();
            dto.setId(libro.getId());
            dto.setTitulo(libro.getTitulo());
            dto.setIsbn(libro.getIsbn());
            dto.setAnioPublicacion(libro.getAnioPublicacion());
            dto.setAutorId(libro.getAutorId());
            dto.setCategoriaId(libro.getCategoriaId());

            // Buscar nombre del autor
            if (libro.getAutorId() != null) {
                autores.stream()
                    .filter(a -> a.getId().equals(libro.getAutorId()))
                    .findFirst()
                    .ifPresent(a -> dto.setAutorNombre(a.getNombre()));
            }

            // Buscar nombre de la categoría
            if (libro.getCategoriaId() != null) {
                categorias.stream()
                    .filter(c -> c.getId().equals(libro.getCategoriaId()))
                    .findFirst()
                    .ifPresent(c -> dto.setCategoriaNombre(c.getNombre()));
            }

            // Contar ejemplares disponibles
            long disponibles = ejemplares.stream()
                .filter(e -> e.getLibroId().equals(libro.getId()) 
                    && e.getEstado() == co.edu.univalle.desarrolloIII.service_inventario.enums.EstadoEjemplar.DISPONIBLE)
                .count();
            dto.setEjemplaresDisponibles((int) disponibles);

            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    public Optional<Libro> findById(Long id) {
        return libroRepository.findById(id);
    }

    @Transactional
    public Libro save(Libro libro) {
        return libroRepository.save(libro);
    }

    @Transactional
    public void deleteById(Long id) {
        libroRepository.deleteById(id);
    }

    @Transactional
    public Libro saveWithAutorAndCategoria(String titulo, String isbn, Integer anioPublicacion, 
                                          String autorNombre, String categoriaNombre, Integer ejemplares) {
        // Buscar o crear autor
        Autor autor = autorRepository.findByNombreIgnoreCase(autorNombre)
                .orElseGet(() -> {
                    Autor nuevoAutor = new Autor();
                    nuevoAutor.setNombre(autorNombre);
                    return autorRepository.save(nuevoAutor);
                });

        // Buscar o crear categoría
        Categoria categoria = categoriaRepository.findByNombreIgnoreCase(categoriaNombre)
                .orElseGet(() -> {
                    Categoria nuevaCategoria = new Categoria();
                    nuevaCategoria.setNombre(categoriaNombre);
                    return categoriaRepository.save(nuevaCategoria);
                });

        // Crear el libro
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setIsbn(isbn);
        libro.setAnioPublicacion(anioPublicacion);
        libro.setAutorId(autor.getId());
        libro.setCategoriaId(categoria.getId());
        Libro libroGuardado = libroRepository.save(libro);

        // Crear ejemplares si se especificó cantidad
        if (ejemplares != null && ejemplares > 0) {
            for (int i = 0; i < ejemplares; i++) {
                Ejemplar ejemplar = new Ejemplar();
                ejemplar.setLibroId(libroGuardado.getId());
                ejemplar.setEstado(co.edu.univalle.desarrolloIII.service_inventario.enums.EstadoEjemplar.DISPONIBLE);
                ejemplar.setCodigoInventario("LIB-" + libroGuardado.getId() + "-" + (i + 1));
                ejemplarRepository.save(ejemplar);
            }
        }

        return libroGuardado;
    }
}