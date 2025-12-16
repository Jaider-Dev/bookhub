package co.edu.univalle.desarrolloIII.service_prestamos.service;

import co.edu.univalle.desarrolloIII.service_prestamos.dto.EjemplarDto;
import co.edu.univalle.desarrolloIII.service_prestamos.dto.LibroDto;
import co.edu.univalle.desarrolloIII.service_prestamos.dto.PrestamoResponseDto;
import co.edu.univalle.desarrolloIII.service_prestamos.dto.UsuarioDto;
import co.edu.univalle.desarrolloIII.service_prestamos.enums.EstadoPrestamo;
import co.edu.univalle.desarrolloIII.service_prestamos.model.Prestamo;
import co.edu.univalle.desarrolloIII.service_prestamos.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

// Importaciones Reactivas
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.time.LocalDate;
@Service
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private WebClient webClient;

    public Mono<Prestamo> createPrestamo(Prestamo prestamo, String authHeader) {
        return this.validateUsuario(prestamo.getUsuarioId(), authHeader)
                .flatMap(usuario -> {
                    if (usuario == null || !usuario.isActivo()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no está activo."));
                    }
                    return this.validateEjemplar(prestamo.getEjemplarId(), authHeader);
                })
                .flatMap(ejemplar -> {
                    if (ejemplar == null || !ejemplar.getEstado().equals("DISPONIBLE")) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "El ejemplar no está disponible para préstamo."));
                    }
                    // 3. Actualizar estado del Ejemplar (service-inventario)
                    return this.updateEjemplarEstado(prestamo.getEjemplarId(), "PRESTADO", authHeader)
                            .thenReturn(prestamo);
                })
                .flatMap(p -> {
                    return Mono.fromCallable(() -> prestamoRepository.save(p))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    public Mono<Prestamo> devolverPrestamo(Long prestamoId, String authHeader) {
        Mono<Prestamo> prestamoMono = Mono.fromCallable(() -> prestamoRepository.findById(prestamoId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optionalPrestamo -> optionalPrestamo.map(Mono::just)
                        .orElseGet(() -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo ID no encontrado."))));

        return prestamoMono
                .flatMap(prestamo -> {
                    if (prestamo.getFechaDevolucionReal() != null) {
                        return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "Este préstamo ya fue devuelto previamente."));
                    }

                    Long ejemplarId = prestamo.getEjemplarId();

                    return this.updateEjemplarEstado(ejemplarId, "DISPONIBLE", authHeader)
                            .thenReturn(prestamo);
                })
                .flatMap(prestamo -> {
                    prestamo.setFechaDevolucionReal(LocalDate.now());
                    prestamo.setEstado(EstadoPrestamo.DEVUELTO);
                    return Mono.fromCallable(() -> prestamoRepository.save(prestamo))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }


    public Mono<Optional<Prestamo>> obtenerPrestamoPorId(Long id) {
        return Mono.fromCallable(() -> prestamoRepository.findById(id));
    }

    private Mono<UsuarioDto> validateUsuario(Long usuarioId, String authHeader) {
        return webClient.get()
                .uri("/usuarios/{id}", usuarioId)
                .header("Authorization", authHeader)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario ID no encontrado.")))
                .onStatus(status -> status.isError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio de usuarios.")))
                .bodyToMono(UsuarioDto.class)
                .onErrorResume(t -> {
                    log.error("FALLO DE CONEXIÓN CRÍTICO con el servicio de Usuarios/Gateway.", t);
                    return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallo de conexión con el servicio de Usuarios."));
                });
    }

    private Mono<EjemplarDto> validateEjemplar(Long ejemplarId, String authHeader) {
        return webClient.get()
                .uri("/inventario/ejemplares/{id}", ejemplarId)
                .header("Authorization", authHeader)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ejemplar ID no encontrado.")))
                .onStatus(status -> status.isError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio de inventario.")))
                .bodyToMono(EjemplarDto.class)
                .onErrorResume(t -> {
                    log.error("FALLO DE CONEXIÓN CRÍTICO con el servicio de Inventario/Gateway.", t);
                    return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallo de conexión con el servicio de Inventario."));
                });
    }

    private Mono<Void> updateEjemplarEstado(Long ejemplarId, String nuevoEstado, String authHeader) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/inventario/ejemplares/{id}/estado")
                        .queryParam("nuevoEstado", nuevoEstado)
                        .build(ejemplarId))
                .header("Authorization", authHeader)
                .retrieve()
                .onStatus(status -> status.isError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fallo al actualizar el inventario.")))
                .toBodilessEntity()
                .then()
                .onErrorResume(t -> {
                    log.error("FALLO DE CONEXIÓN CRÍTICO al actualizar el Inventario/Gateway.", t);
                    return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallo de conexión al actualizar el Inventario."));
                });
    }

    public java.util.List<Prestamo> findAll() {
        return prestamoRepository.findAll();
    }

    public Flux<PrestamoResponseDto> findAllWithDetails(String authHeader) {
        return Flux.fromIterable(prestamoRepository.findAll())
                .flatMap(prestamo ->
                        Mono.zip(
                                validateUsuario(prestamo.getUsuarioId(), authHeader),
                                validateEjemplar(prestamo.getEjemplarId(), authHeader)
                        )
                        .flatMap(tuple -> {
                            UsuarioDto usuario = tuple.getT1();
                            EjemplarDto ejemplar = tuple.getT2();

                            return getLibroById(ejemplar.getLibroId(), authHeader)
                                    .map(libro -> {
                                        PrestamoResponseDto dto = new PrestamoResponseDto();
                                        dto.setId(prestamo.getId());
                                        dto.setUsuarioId(prestamo.getUsuarioId());
                                        dto.setUsuarioEmail(usuario.getEmail());
                                        dto.setEjemplarId(prestamo.getEjemplarId());
                                        dto.setLibroTitulo(libro.getTitulo());
                                        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
                                        dto.setFechaVencimiento(prestamo.getFechaVencimiento());
                                        dto.setFechaDevolucionReal(prestamo.getFechaDevolucionReal());
                                        dto.setEstado(prestamo.getEstado() != null ? prestamo.getEstado() : EstadoPrestamo.ACTIVO);
                                        return dto;
                                    });
                        })
                        .onErrorResume(e -> {
                            log.error("Error al obtener detalles para préstamo {}: {}", prestamo.getId(), e.getMessage());
                            // Devolver un DTO parcial si falla la obtención de detalles
                            PrestamoResponseDto dto = new PrestamoResponseDto();
                            dto.setId(prestamo.getId());
                            dto.setUsuarioId(prestamo.getUsuarioId());
                            dto.setEjemplarId(prestamo.getEjemplarId());
                            dto.setFechaPrestamo(prestamo.getFechaPrestamo());
                            dto.setFechaVencimiento(prestamo.getFechaVencimiento());
                            dto.setFechaDevolucionReal(prestamo.getFechaDevolucionReal());
                            dto.setEstado(prestamo.getEstado() != null ? prestamo.getEstado() : EstadoPrestamo.ACTIVO);
                            dto.setUsuarioEmail("Error");
                            dto.setLibroTitulo("Error");
                            return Mono.just(dto);
                        })
                );
    }

    private Mono<LibroDto> getLibroById(Long libroId, String authHeader) {
        return webClient.get()
                .uri("/inventario/libros/{id}", libroId)
                .header("Authorization", authHeader)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Libro ID no encontrado.")))
                .onStatus(status -> status.isError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio de inventario.")))
                .bodyToMono(LibroDto.class)
                .onErrorResume(t -> {
                    log.error("FALLO DE CONEXIÓN CRÍTICO con el servicio de Inventario/Gateway para obtener libro.", t);
                    return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallo de conexión con el servicio de Inventario."));
                });
    }
}