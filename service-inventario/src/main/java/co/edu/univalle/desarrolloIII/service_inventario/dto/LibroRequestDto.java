package co.edu.univalle.desarrolloIII.service_inventario.dto;

import lombok.Data;

@Data
public class LibroRequestDto {
    private String titulo;
    private String isbn;
    private Integer anioPublicacion;
    private String autorNombre;  // Nombre del autor en lugar de ID
    private String categoriaNombre;  // Nombre de la categoría en lugar de ID
    private Integer ejemplares;  // Cantidad de ejemplares a crear
}


