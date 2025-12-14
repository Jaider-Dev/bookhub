package co.edu.univalle.desarrolloIII.service_prestamos.dto;

import lombok.Data;

@Data
public class UsuarioDto {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String cedula;

    private boolean activo;
}