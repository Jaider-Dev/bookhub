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
        // Validar que no exista otro usuario con la misma cédula
        if (usuario.getCedula() != null && !usuario.getCedula().isEmpty()) {
            Optional<Usuario> usuarioConCedula = usuarioRepository.findByCedula(usuario.getCedula());
            if (usuarioConCedula.isPresent() && !usuarioConCedula.get().getId().equals(usuario.getId())) {
                throw new RuntimeException("Ya existe un usuario con esta cédula");
            }
        }

        // Validar que no exista otro usuario con el mismo email
        Optional<Usuario> usuarioConEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioConEmail.isPresent() && !usuarioConEmail.get().getId().equals(usuario.getId())) {
            throw new RuntimeException("Ya existe un usuario con este correo electrónico");
        }

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
                    // Validar cédula única si se está cambiando
                    if (usuarioDetails.getCedula() != null && !usuarioDetails.getCedula().isEmpty()) {
                        Optional<Usuario> usuarioConCedula = usuarioRepository.findByCedula(usuarioDetails.getCedula());
                        if (usuarioConCedula.isPresent() && !usuarioConCedula.get().getId().equals(id)) {
                            throw new RuntimeException("Ya existe un usuario con esta cédula");
                        }
                        usuarioExistente.setCedula(usuarioDetails.getCedula());
                    }

                    // Validar email único si se está cambiando
                    if (usuarioDetails.getEmail() != null
                            && !usuarioDetails.getEmail().equals(usuarioExistente.getEmail())) {
                        Optional<Usuario> usuarioConEmail = usuarioRepository.findByEmail(usuarioDetails.getEmail());
                        if (usuarioConEmail.isPresent() && !usuarioConEmail.get().getId().equals(id)) {
                            throw new RuntimeException("Ya existe un usuario con este correo electrónico");
                        }
                        usuarioExistente.setEmail(usuarioDetails.getEmail());
                    }

                    // Actualiza los campos necesarios
                    usuarioExistente.setNombre(usuarioDetails.getNombre());
                    if (usuarioDetails.getTelefono() != null) {
                        usuarioExistente.setTelefono(usuarioDetails.getTelefono());
                    }
                    if (usuarioDetails.getRol() != null) {
                        usuarioExistente.setRol(usuarioDetails.getRol());
                    }
                    // Validar estado activo
                    if (usuarioDetails.getActivo() != null
                            && !usuarioDetails.getActivo().equals(usuarioExistente.getActivo())) {
                        usuarioExistente.setActivo(usuarioDetails.getActivo());
                    }
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