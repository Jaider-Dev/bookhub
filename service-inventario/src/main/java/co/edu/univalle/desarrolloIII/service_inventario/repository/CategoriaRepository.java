package co.edu.univalle.desarrolloIII.service_inventario.repository;

import co.edu.univalle.desarrolloIII.service_inventario.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}