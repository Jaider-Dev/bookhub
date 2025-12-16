package co.edu.univalle.desarrolloIII.service_prestamos.dto;

import co.edu.univalle.desarrolloIII.service_prestamos.enums.EstadoPrestamo;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PrestamoResponseDto {
    private Long id;
    private Long usuarioId;
    private String usuarioEmail;
    private Long ejemplarId;
    private Long libroId;
    private String libroTitulo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucionReal;
    private EstadoPrestamo estado;
}

