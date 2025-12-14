package co.edu.univalle.desarrolloIII.bookhub_gateway.filters;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "auth-security")
public class AuthSecurityProperties {

    private List<SecurityPath> adminPaths = List.of();

    public List<SecurityPath> getAdminPaths() {
        return adminPaths;
    }

    public void setAdminPaths(List<SecurityPath> adminPaths) {
        this.adminPaths = adminPaths;
    }

    public static class SecurityPath {
        private String path;
        private List<String> methods = List.of();

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public List<String> getMethods() { return methods; }
        public void setMethods(List<String> methods) { this.methods = methods; }
    }
}