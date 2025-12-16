package co.edu.univalle.desarrolloIII.service_usuarios.controller;

import co.edu.univalle.desarrolloIII.service_usuarios.model.Usuario;
import co.edu.univalle.desarrolloIII.service_usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    public static class PasswordRequest {
        private String nuevaPassword;

        public String getNuevaPassword() { return nuevaPassword; }
        public void setNuevaPassword(String nuevaPassword) { this.nuevaPassword = nuevaPassword; }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {
        try {
            // Establecer rol por defecto si no se especifica (para registro público)
            if (usuario.getRol() == null || usuario.getRol().isEmpty()) {
                usuario.setRol("LECTOR");
            }
            Usuario savedUsuario = usuarioService.guardarUsuario(usuario);
            return new ResponseEntity<>(savedUsuario, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPublic(@RequestBody Usuario usuario) {
        // Endpoint público para registro de usuarios
        try {
            usuario.setRol("LECTOR"); // Los usuarios que se registran son siempre LECTOR
            usuario.setActivo(true);
            Usuario savedUsuario = usuarioService.guardarUsuario(usuario);
            return new ResponseEntity<>(savedUsuario, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public List<Usuario> getAll() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Usuario usuarioDetails) {
        try {
            return usuarioService.actualizarUsuario(id, usuarioDetails)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    
    @PatchMapping("/{id}/Password")
    public ResponseEntity<Usuario> cambiarPassword(@PathVariable Long id,
                                                     @RequestBody PasswordRequest request) {

        if (request.getNuevaPassword() == null || request.getNuevaPassword().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return usuarioService.cambiarPassword(id, request.getNuevaPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deshabilitar(@PathVariable Long id) {
        if (usuarioService.desabilitarUsuario(id).isPresent()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}