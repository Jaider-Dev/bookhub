package co.edu.univalle.desarrolloIII.service_usuarios.service;

import co.edu.univalle.desarrolloIII.service_usuarios.model.Usuario;
import co.edu.univalle.desarrolloIII.service_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getPassword() != null) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> desabilitarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    usuarioExistente.setActivo(false);
                    return usuarioRepository.save(usuarioExistente);
                });
    }

    public Optional<Usuario> actualizarUsuario(Long id, Usuario usuarioDetails) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    // Actualiza los campos necesarios
                    usuarioExistente.setNombre(usuarioDetails.getNombre());
                    usuarioExistente.setEmail(usuarioDetails.getEmail());
                    usuarioExistente.setRol(usuarioDetails.getRol());
                    return usuarioRepository.save(usuarioExistente);
                });
    }

    public Optional<Usuario> cambiarPassword(Long id, String nuevaContrasena) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    String contrasenaHasheada = passwordEncoder.encode(nuevaContrasena);
                    usuarioExistente.setPassword(contrasenaHasheada);
                    return usuarioRepository.save(usuarioExistente);
                });
    }
}