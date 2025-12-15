package co.edu.univalle.desarrolloIII.service_usuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre")
    private String nombre;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "telefono")
    private String telefono;
    
    @Column(name = "cedula")
    private String cedula;
    
    @Column(name = "contrasena")
    private String password;

    @Column(name = "activo")
    private boolean activo = true;

    @Column(name = "rol")
    private String rol;
}