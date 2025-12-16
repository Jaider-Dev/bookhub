package co.edu.univalle.desarrolloIII.service_usuarios.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.email.from:noreply@bookhub.co}")
    private String fromEmail;

    @Value("${app.email.password-recovery.url:http://localhost:4200/password-recovery}")
    private String passwordRecoveryUrl;

    public void sendPasswordRecoveryEmail(String toEmail, String recoveryToken) {
        if (!emailEnabled || mailSender == null) {
            // Fallback: log the token for development/testing
            log.info("=== PASSWORD RECOVERY TOKEN ===");
            log.info("Email: {}", toEmail);
            log.info("Token: {}", recoveryToken);
            log.info("Recovery URL: {}?email={}&token={}", passwordRecoveryUrl, toEmail, recoveryToken);
            log.info("===============================");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Recuperación de Contraseña - BookHub");
            message.setText(String.format(
                "Hola,\n\n" +
                "Has solicitado recuperar tu contraseña en BookHub.\n\n" +
                "Para restablecer tu contraseña, utiliza el siguiente token:\n\n" +
                "%s\n\n" +
                "O visita el siguiente enlace:\n" +
                "%s?email=%s&token=%s\n\n" +
                "Este token expirará en 1 hora.\n\n" +
                "Si no solicitaste este cambio, ignora este correo.\n\n" +
                "Saludos,\n" +
                "Equipo BookHub",
                recoveryToken,
                passwordRecoveryUrl,
                toEmail,
                recoveryToken
            ));

            mailSender.send(message);
            log.info("Email de recuperación enviado a: {}", toEmail);
        } catch (Exception e) {
            log.error("Error enviando email de recuperación a {}: {}", toEmail, e.getMessage());
            // Fallback to logging
            log.info("=== PASSWORD RECOVERY TOKEN (fallback) ===");
            log.info("Email: {}", toEmail);
            log.info("Token: {}", recoveryToken);
            log.info("Recovery URL: {}?email={}&token={}", passwordRecoveryUrl, toEmail, recoveryToken);
            log.info("===========================================");
        }
    }
}


