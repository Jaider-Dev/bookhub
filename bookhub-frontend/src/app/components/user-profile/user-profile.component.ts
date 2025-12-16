import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Auth } from '../../services/auth';
import { UsuariosService, Usuario } from '../../services/usuarios.service';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.css']
})
export class UserProfileComponent implements OnInit {
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  formData: Partial<Usuario> = {
    nombre: '',
    email: '',
    telefono: '',
    cedula: ''
  };

  // Password change fields
  currentPassword = '';
  newPassword = '';
  confirmNewPassword = '';

  private currentUserId: number | null = null;
  private currentUserRole: 'ADMIN' | 'LECTOR' | null = null;

  constructor(private auth: Auth, private usuariosService: UsuariosService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const email = this.auth.getEmailFromToken();
    if (!email) {
      this.errorMessage = 'No se pudo determinar el usuario actual.';
      return;
    }

    this.isLoading = true;
    this.usuariosService.getUsuarios().subscribe({
      next: (users) => {
        const me = users.find(u => u.email === email);
        if (!me) {
          this.errorMessage = 'Usuario no encontrado.';
        } else {
          this.currentUserId = me.id || null;
          this.currentUserRole = me.rol || null;
          this.formData = {
            nombre: me.nombre,
            email: me.email,
            telefono: me.telefono || '',
            cedula: me.cedula || ''
          };
        }
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error cargando perfil.';
        console.error('Error cargando perfil:', err);
        this.isLoading = false;
      }
    });
  }

  save(): void {
    this.errorMessage = '';
    this.successMessage = '';
    if (!this.currentUserId) {
      this.errorMessage = 'ID de usuario no disponible.';
      return;
    }

    const payload: Usuario = {
      id: this.currentUserId,
      nombre: this.formData.nombre || '',
      email: this.formData.email || '',
      telefono: this.formData.telefono || '',
      cedula: this.formData.cedula || '',
      activo: true,
      rol: this.currentUserRole || 'LECTOR'
    };

    this.usuariosService.updateUsuario(this.currentUserId, payload).subscribe({
      next: () => {
        this.successMessage = '✅ Perfil actualizado correctamente.';
        setTimeout(() => this.successMessage = '', 5000);
      },
      error: (err) => {
        console.error('Error actualizando perfil:', err);
        const errorMsg = typeof err.error === 'string' ? err.error : (err.error?.message || 'Error al actualizar el perfil.');
        this.errorMessage = errorMsg;
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }

  changePassword(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.currentUserId) {
      this.errorMessage = 'ID de usuario no disponible.';
      return;
    }

    if (!this.currentPassword || !this.newPassword) {
      this.errorMessage = 'Completa ambos campos de contraseña.';
      return;
    }

    if (this.newPassword !== this.confirmNewPassword) {
      this.errorMessage = 'La nueva contraseña y su confirmación no coinciden.';
      return;
    }

    this.isLoading = true;
    this.usuariosService.changePassword(this.currentUserId, this.currentPassword, this.newPassword).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = 'Contraseña cambiada correctamente.';
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmNewPassword = '';
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error cambiando contraseña:', err);
        this.errorMessage = err.error?.message || 'Error al cambiar la contraseña.';
      }
    });
  }
}
