package co.edu.univalle.desarrolloIII.service_inventario.model;

import co.edu.univalle.desarrolloIII.service_inventario.enums.EstadoEjemplar;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long libroId;

    private String codigoInventario;

    @Enumerated(EnumType.STRING)
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

}