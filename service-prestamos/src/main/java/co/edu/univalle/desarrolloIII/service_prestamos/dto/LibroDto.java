package co.edu.univalle.desarrolloIII.service_prestamos.dto;

import lombok.Data;

@Data
public class LibroDto {
    private Long id;
    private String titulo;
    private String isbn;
    private Integer anioPublicacion;
}


