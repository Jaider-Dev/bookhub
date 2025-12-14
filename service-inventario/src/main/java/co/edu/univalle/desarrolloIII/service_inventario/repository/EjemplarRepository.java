package co.edu.univalle.desarrolloIII.service_inventario.repository;

import co.edu.univalle.desarrolloIII.service_inventario.model.Ejemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjemplarRepository extends JpaRepository<Ejemplar, Long> {
}