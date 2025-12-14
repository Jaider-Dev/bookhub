package co.edu.univalle.desarrolloIII.service_prestamos.model;

import co.edu.univalle.desarrolloIII.service_prestamos.enums.EstadoPrestamo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;

    private Long ejemplarId;

    private LocalDate fechaPrestamo = LocalDate.now();

    private LocalDate fechaVencimiento = LocalDate.now().plusDays(15);

    private LocalDate fechaDevolucionReal;

    @Enumerated(EnumType.STRING)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;
}