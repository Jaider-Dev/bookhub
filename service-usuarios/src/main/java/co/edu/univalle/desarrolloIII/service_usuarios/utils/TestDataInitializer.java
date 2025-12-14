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

            if (repository.findByEmail("admin@bookhub.co").isEmpty()) {

                String rawPassword = "adminpassword";

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

                System.out.println("-----------------------------------------------------------------");
                System.out.println("🚀 USUARIO DE PRUEBA CREADO EN LA BASE DE DATOS:");
                System.out.println("   - Email: admin@bookhub.co");
                System.out.println("   - Contraseña: adminpassword");
                System.out.println("   - Rol: ADMIN");
                System.out.println("-----------------------------------------------------------------");
            }
        };
    }
}