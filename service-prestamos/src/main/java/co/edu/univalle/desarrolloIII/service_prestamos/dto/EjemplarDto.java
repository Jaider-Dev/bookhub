package co.edu.univalle.desarrolloIII.service_prestamos.dto;

import lombok.Data;

@Data
public class EjemplarDto {
    private Long id;
    private Long libroId;
    private String codigoInventario;
    private String estado;
}