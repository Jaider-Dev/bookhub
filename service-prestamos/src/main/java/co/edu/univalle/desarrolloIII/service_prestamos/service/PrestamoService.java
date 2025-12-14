package co.edu.univalle.desarrolloIII.service_prestamos.service;

import co.edu.univalle.desarrolloIII.service_prestamos.dto.EjemplarDto;
import co.edu.univalle.desarrolloIII.service_prestamos.dto.UsuarioDto;
import co.edu.univalle.desarrolloIII.service_prestamos.model.Prestamo;
import co.edu.univalle.desarrolloIII.service_prestamos.repository.PrestamoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; // Importar esta clase
import org.springframework.web.server.ResponseStatusException;

@Service
public class PrestamoService {

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Autowired
    private WebClient webClient;

    public Prestamo createPrestamo(Prestamo prestamo, String authHeader) {

        // 1. Validar Usuario (service-usuarios)
        UsuarioDto usuario = this.validateUsuario(prestamo.getUsuarioId(), authHeader);
        if (usuario == null || !usuario.isActivo()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no está activo.");
        }

        // 2. Validar Ejemplar (service-inventario)
        EjemplarDto ejemplar = this.validateEjemplar(prestamo.getEjemplarId(), authHeader);
        if (ejemplar == null || !ejemplar.getEstado().equals("DISPONIBLE")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El ejemplar no está disponible para préstamo.");
        }

        // 3. Actualizar estado del Ejemplar (service-inventario)
        this.updateEjemplarEstado(prestamo.getEjemplarId(), "PRESTADO", authHeader);

        // 4. Registro del Préstamo
        return prestamoRepository.save(prestamo);
    }

    // =======================================================
    // MÉTODOS AUXILIARES DE COMUNICACIÓN
    // =======================================================

    private UsuarioDto validateUsuario(Long usuarioId, String authHeader) {
        try {
            return webClient.get()
                    .uri("/usuarios/{id}", usuarioId)
                    .header("Authorization", authHeader)
                    .retrieve()
                    .bodyToMono(UsuarioDto.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario ID no encontrado en el sistema.", e);
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio de usuarios: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallo de conexión con el servicio de Usuarios.");
        }
    }

    private EjemplarDto validateEjemplar(Long ejemplarId, String authHeader) {
        try {
            return webClient.get()
                    .uri("/inventario/ejemplares/{id}", ejemplarId)
                    .header("Authorization", authHeader)
                    .retrieve()
                    .bodyToMono(EjemplarDto.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ejemplar ID no encontrado en el sistema.", e);
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el servicio de inventario: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallo de conexión con el servicio de Inventario.");
        }
    }

    private void updateEjemplarEstado(Long ejemplarId, String nuevoEstado, String authHeader) {
        try {
            webClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/inventario/ejemplares/{id}/estado")
                            .queryParam("nuevoEstado", nuevoEstado)
                            .build(ejemplarId))
                    .header("Authorization", authHeader)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Fallo al actualizar el inventario: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Fallo de conexión al actualizar el Inventario.");
        }
    }
}