package co.edu.univalle.desarrolloIII.service_inventario.dto;

import lombok.Data;

@Data
public class LibroResponseDto {
    private Long id;
    private String titulo;
    private String isbn;
    private Integer anioPublicacion;
    private String autorNombre;
    private String categoriaNombre;
    private Long autorId;
    private Long categoriaId;
    private Integer ejemplaresDisponibles;
}


