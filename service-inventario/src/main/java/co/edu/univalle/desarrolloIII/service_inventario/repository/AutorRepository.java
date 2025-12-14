package co.edu.univalle.desarrolloIII.service_inventario.repository;

import co.edu.univalle.desarrolloIII.service_inventario.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
}