package co.edu.univalle.desarrolloIII.service_usuarios.repository;

import co.edu.univalle.desarrolloIII.service_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCedula(String cedula);
    Optional<Usuario> findByEmail(String email);
}