import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UsuariosService, Usuario } from '../../services/usuarios.service';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent implements OnInit {
  formData: Partial<Usuario> = {
    nombre: '',
    email: '',
    telefono: '',
    cedula: '',
    password: '',
    activo: true,
    rol: 'LECTOR'
  };
  
  confirmPassword = '';
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private usuariosService: UsuariosService,
    private authService: Auth,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/reader']);
    }
  }

  onRegister(): void {
    this.errorMessage = '';
    this.successMessage = '';
    
    // Validaciones
    if (!this.formData.nombre || !this.formData.email || !this.formData.password) {
      this.errorMessage = 'Nombre, email y contraseña son requeridos';
      return;
    }

    if (this.formData.password !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    if (this.formData.password!.length < 6) {
      this.errorMessage = 'La contraseña debe tener al menos 6 caracteres';
      return;
    }

    this.isLoading = true;

    const usuario: Usuario = {
      nombre: this.formData.nombre!,
      email: this.formData.email!,
      telefono: this.formData.telefono || null,
      cedula: this.formData.cedula || null,
      password: this.formData.password!,
      activo: true,
      rol: 'LECTOR'
    };

    this.usuariosService.createPublicUsuario(usuario).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = '✅ Registro exitoso. Redirigiendo al login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error en registro:', err);
        if (err.status === 409) {
          this.errorMessage = err.error || 'Ya existe un usuario con este correo o cédula';
        } else {
          this.errorMessage = err.error?.message || 'Error al registrar usuario. Intenta de nuevo.';
        }
      }
    });
  }
}


