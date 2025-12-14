package co.edu.univalle.desarrolloIII.service_usuarios.controller;

import co.edu.univalle.desarrolloIII.service_usuarios.model.Usuario;
import co.edu.univalle.desarrolloIII.service_usuarios.security.JwtUtil;
import co.edu.univalle.desarrolloIII.service_usuarios.service.UsuarioService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Data
    private static class LoginRequest {
        private String email;
        private String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Usuario> usuarioOptional = usuarioService.findByEmail(loginRequest.getEmail());

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas: Usuario no encontrado.");
        }

        Usuario usuario = usuarioOptional.get();

        // 2. Verificar la contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            // Contraseña incorrecta
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas: Contraseña incorrecta.");
        }

        // 3. Generar el Token JWT si la autenticación es exitosa
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());

        // 4. Devolver el token al cliente
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Data
    private static class AuthResponse {
        private final String token;
        private final String tipo = "Bearer";
    }
}