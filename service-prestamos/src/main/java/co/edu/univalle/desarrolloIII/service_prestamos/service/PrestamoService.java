package co.edu.univalle.desarrolloIII.service_prestamos.service;

import co.edu.univalle.desarrolloIII.service_prestamos.dto.EjemplarDto;
import co.edu.univalle.desarrolloIII.service_prestamos.dto.UsuarioDto;
import co.edu.univalle.desarrolloIII.service_prestamos.model.Prestamo;
import co.edu.univalle.desarrolloIII.service_prestamos.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

// Importaciones Reactivas
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
        // 1. Validar Usuario (service-usuarios)
        return this.validateUsuario(prestamo.getUsuarioId(), authHeader)
                .flatMap(usuario -> {
                    if (usuario == null || !usuario.isActivo()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no está activo."));
                    }
                    // 2. Validar Ejemplar (service-inventario)
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
                    // 4. Registrar el Préstamo (Operación síncrona de JPA)
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
}