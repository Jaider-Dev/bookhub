import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router'; // Añadido RouterModule
import { FormsModule } from '@angular/forms'; // Módulo para [(ngModel)]
import { CommonModule } from '@angular/common'; // Módulo para *ngIf
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true, // ¡CLAVE: Es un componente autónomo!
  imports: [FormsModule, CommonModule, RouterModule], // Importa las dependencias del HTML
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent implements OnInit {
  credentials = {
    email: '',
    password: ''
  };
  loginError = '';
  isLoading = false;
  
  constructor(private authService: Auth, private router: Router) { }

  onLogin(): void {
    this.loginError = '';
    this.isLoading = true;

    this.authService.login(this.credentials).subscribe({
      next: () => {
        this.isLoading = false;
        console.log('✅ Login exitoso');
        if (this.authService.isAdmin()) {
          console.log('🔐 Redirigiendo a /admin (Usuario es ADMIN)');
          this.router.navigate(['/admin']);
        } else {
          console.log('📖 Redirigiendo a /reader (Usuario es READER)');
          this.router.navigate(['/reader']);
        }
      },
      error: (err) => {
        this.isLoading = false;
        console.error('❌ Error de autenticación:', err);
        // Mostrar mensaje del servidor cuando sea una cadena o un objeto con message
        const serverMsg = typeof err.error === 'string'
          ? err.error
          : err.error?.message;

        if (err.status === 401 || err.status === 403) {
          this.loginError = serverMsg || 'Correo o contraseña incorrecta.';
        } else if (err.status === 0) {
          this.loginError = 'No se pudo conectar con el servidor. Verifica tu conexión.';
        } else {
          this.loginError = serverMsg || 'Ocurrió un error al intentar iniciar sesión.';
        }
      }
    });
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      if (this.authService.isAdmin()) {
        this.router.navigate(['/admin']);
      } else {
        this.router.navigate(['/reader']);
      }
    }
  }
}