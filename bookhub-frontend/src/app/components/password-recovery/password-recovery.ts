import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-password-recovery',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterModule],
  templateUrl: './password-recovery.html',
  styleUrls: ['./password-recovery.css']
})
export class PasswordRecoveryComponent {
  email = '';
  recoveryToken = '';
  newPassword = '';
  confirmPassword = '';
  step: 'request' | 'reset' = 'request';
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  solicitarRecuperacion(): void {
    this.errorMessage = '';
    this.successMessage = '';
    
    if (!this.email) {
      this.errorMessage = 'El email es requerido';
      return;
    }

    this.isLoading = true;
    this.http.post('http://localhost:8080/usuarios/recuperar-password', { email: this.email })
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.successMessage = 'Si el email existe, se enviará un enlace de recuperación. Por ahora, revisa la consola del servidor para obtener el token.';
          setTimeout(() => {
            this.step = 'reset';
          }, 2000);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = 'Error al solicitar recuperación de contraseña';
          console.error('Error:', err);
        }
      });
  }

  resetPassword(): void {
    this.errorMessage = '';
    this.successMessage = '';
    
    if (!this.email || !this.recoveryToken || !this.newPassword) {
      this.errorMessage = 'Todos los campos son requeridos';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    if (this.newPassword.length < 6) {
      this.errorMessage = 'La contraseña debe tener al menos 6 caracteres';
      return;
    }

    this.isLoading = true;
    this.http.post('http://localhost:8080/usuarios/reset-password', {
      email: this.email,
      token: this.recoveryToken,
      nuevaPassword: this.newPassword
    }).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = '✅ Contraseña restablecida correctamente. Redirigiendo al login...';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.isLoading = false;
        const errorMsg = typeof err.error === 'string' ? err.error : (err.error?.message || 'Error al restablecer contraseña');
        this.errorMessage = errorMsg;
      }
    });
  }
}


