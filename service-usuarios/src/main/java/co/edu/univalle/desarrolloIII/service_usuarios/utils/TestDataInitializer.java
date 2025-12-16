package co.edu.univalle.desarrolloIII.service_usuarios.utils;

import co.edu.univalle.desarrolloIII.service_usuarios.model.Usuario;
import co.edu.univalle.desarrolloIII.service_usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class TestDataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository repository, PasswordEncoder passwordEncoder) {

        return args -> {
            try {
                System.out.println("---- INICIANDO CARGA DE DATOS DE PRUEBA ----");
                String rawPassword = "adminpassword";

                // Si no existe, crear; si existe, forzar la contraseña a la conocida de prueba
                repository.findByEmail("admin@bookhub.co").ifPresentOrElse(existing -> {
                    System.out.println("---- USUARIO ADMIN ENCONTRADO, ACTUALIZANDO ... ----");
                    existing.setPassword(passwordEncoder.encode(rawPassword));
                    existing.setRol("ADMIN");
                    repository.save(existing);
                    System.out.println("---- USUARIO ADMIN ACTUALIZADO CORRECTAMENTE ----");
                }, () -> {
                    System.out.println("---- USUARIO ADMIN NO ENCONTRADO, CREANDO ... ----");
                    String hashedPassword = passwordEncoder.encode(rawPassword);

                    Usuario admin = Usuario.builder()
                            .nombre("Administrador Principal")
                            .email("admin@bookhub.co")
                            .password(hashedPassword)
                            .cedula("0000000000")
                            .rol("ADMIN")
                            .activo(true)
                            .build();
                    repository.save(admin);
                    System.out.println("---- USUARIO ADMIN CREADO CORRECTAMENTE ----");
                });
                System.out.println("---- CARGA DE DATOS DE PRUEBA FINALIZADA ----");
            } catch (Exception e) {
                System.err.println("---- ERROR EN CARGA DE DATOS DE PRUEBA: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}