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
import reactor.core.scheduler.Schedulers; // Para manejar operaciones síncronas de JPA
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private WebClient webClient;

    /**
     * Crea un préstamo orquestando llamadas a servicios de Usuarios e Inventario.
     * Retorna un Mono<Prestamo> para mantener el flujo reactivo.
     */
    public Mono<Prestamo> createPrestamo(Prestamo prestamo, String authHeader) {
        // La lógica de negocio se encadena usando flatMap y Mono.error

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
                    // thenReturn(prestamo) asegura que el objeto 'prestamo' original se pasa al siguiente flatMap
                    return this.updateEjemplarEstado(prestamo.getEjemplarId(), "PRESTADO", authHeader)
                            .thenReturn(prestamo);
                })
                .flatMap(p -> {
                    // 4. Registrar el Préstamo (Operación síncrona de JPA)
                    // Usamos subscribeOn(Schedulers.boundedElastic()) para mover la operación bloqueante
                    // a un pool de hilos separado, liberando los hilos reactivos.
                    return Mono.fromCallable(() -> prestamoRepository.save(p))
                            .subscribeOn(Schedulers.boundedElastic());
                });
    }

    // =======================================================
    // MÉTODOS AUXILIARES DE COMUNICACIÓN (REACTIVOS)
    // =======================================================

    private Mono<UsuarioDto> validateUsuario(Long usuarioId, String authHeader) {
        return webClient.get()
                .uri("/usuarios/{id}", usuarioId)
                .header("Authorization", authHeader)
                .retrieve()
                // CORRECCIÓN APLICADA AQUÍ:
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario ID no encontrado.")))
                .onStatus(status -> status.isError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio de usuarios.")))
                // FIN DE CORRECCIÓN
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

                // CORRECCIÓN PARA is4xxClientError (Línea 92)
                .onStatus(status -> status.is4xxClientError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ejemplar ID no encontrado.")))

                // CORRECCIÓN PARA isError
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

                // CORRECCIÓN APLICADA AQUÍ (LÍNEA 116 en tu código original)
                .onStatus(status -> status.isError(), clientResponse ->
                        Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fallo al actualizar el inventario.")))

                .toBodilessEntity()
                .then() // Convierte a Mono<Void>
                // Manejo de errores de red
                .onErrorResume(t -> {
                    log.error("FALLO DE CONEXIÓN CRÍTICO al actualizar el Inventario/Gateway.", t);
                    return Mono.error(new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallo de conexión al actualizar el Inventario."));
                });
    }

    public Mono<Optional<Prestamo>> obtenerPrestamoPorId(Long id) {
        return Mono.fromCallable(() -> prestamoRepository.findById(id));
    }
}