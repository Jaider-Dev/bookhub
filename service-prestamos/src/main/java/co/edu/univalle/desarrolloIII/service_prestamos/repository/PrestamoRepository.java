package co.edu.univalle.desarrolloIII.service_prestamos.repository;

import co.edu.univalle.desarrolloIII.service_prestamos.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
}