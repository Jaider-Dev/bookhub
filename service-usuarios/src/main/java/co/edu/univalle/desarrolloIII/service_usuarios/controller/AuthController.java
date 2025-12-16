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

    @Autowired
    private co.edu.univalle.desarrolloIII.service_usuarios.service.EmailService emailService;

    @Data
    private static class LoginRequest {
        private String email;
        private String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("DEBUG: Intento de login para email: " + loginRequest.getEmail());
        Optional<Usuario> usuarioOptional = usuarioService.findByEmail(loginRequest.getEmail());

        if (usuarioOptional.isEmpty()) {
            System.out.println("DEBUG: Usuario no encontrado: " + loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas: Usuario no encontrado.");
        }

        Usuario usuario = usuarioOptional.get();
        System.out.println("DEBUG: Usuario encontrado. ID: " + usuario.getId() + ", Activo: " + usuario.getActivo());

        // 2. Verificar la contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
            System.out.println("DEBUG: Contraseña incorrecta para email: " + loginRequest.getEmail());
            // Contraseña incorrecta
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas: Contraseña incorrecta.");
        }

        // 3. Generar el Token JWT si la autenticación es exitosa
        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getRol());
        System.out.println("DEBUG: Login exitoso, token generado.");

        // 4. Devolver el token al cliente
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Data
    private static class AuthResponse {
        private final String token;
        private final String tipo = "Bearer";
    }

    @Data
    private static class PasswordRecoveryRequest {
        private String email;
    }

    @Data
    private static class ResetPasswordRequest {
        private String email;
        private String token;
        private String nuevaPassword;
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody PasswordRecoveryRequest request) {
        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(request.getEmail());

        if (usuarioOpt.isEmpty()) {
            // Por seguridad, siempre devolvemos éxito aunque el email no exista
            return ResponseEntity.ok("Si el email existe, se enviará un enlace de recuperación.");
        }

        Usuario usuario = usuarioOpt.get();

        // Generar token de recuperación (en producción, guardar en BD con expiración)
        String recoveryToken = java.util.UUID.randomUUID().toString();

        // Enviar email de recuperación
        emailService.sendPasswordRecoveryEmail(usuario.getEmail(), recoveryToken);

        return ResponseEntity.ok("Si el email existe, se enviará un enlace de recuperación.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        // En producción, validar el token y su expiración desde la BD
        // Por ahora, validamos solo el email

        Optional<Usuario> usuarioOpt = usuarioService.findByEmail(request.getEmail());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        if (request.getNuevaPassword() == null || request.getNuevaPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña debe tener al menos 6 caracteres");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioService.guardarUsuario(usuario);

        return ResponseEntity.ok("Contraseña restablecida correctamente");
    }
}