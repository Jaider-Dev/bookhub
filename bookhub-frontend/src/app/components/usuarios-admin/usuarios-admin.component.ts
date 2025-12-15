import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuariosService, Usuario } from '../../services/usuarios.service';

@Component({
  selector: 'app-usuarios-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuarios-admin.component.html',
  styleUrls: ['./usuarios-admin.component.css']
})
export class UsuariosAdminComponent implements OnInit {
  usuarios: Usuario[] = [];
  usuariosFiltered: Usuario[] = [];
  showForm = false;
  editingId: number | null = null;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  searchTerm = '';
  filterRole: 'ALL' | 'ADMIN' | 'LECTOR' = 'ALL';
  filterActivo: 'ALL' | 'ACTIVO' | 'INACTIVO' = 'ALL';
  sortBy: 'nombre' | 'email' | 'rol' = 'nombre';
  
  formData: Usuario = {
    nombre: '',
    email: '',
    telefono: '',
    cedula: '',
    rol: 'LECTOR',
    activo: true
  };

  constructor(private usuariosService: UsuariosService) {}

  ngOnInit(): void {
    this.loadUsuarios();
  }

  loadUsuarios(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';
    console.log('[UsuariosAdmin] Cargando usuarios...');
    
    this.usuariosService.getUsuarios().subscribe({
      next: (data) => {
        console.log('[UsuariosAdmin] Usuarios cargados:', data.length);
        this.usuarios = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('[UsuariosAdmin] Error cargando usuarios:', error);
        
        if (error.name === 'TimeoutError') {
          this.errorMessage = 'Tiempo de espera agotado. El servidor está tardando. Intenta de nuevo.';
        } else if (error.status === 0) {
          this.errorMessage = 'Error de conexión. Verifica que el servidor esté corriendo en puerto 8080.';
        } else {
          this.errorMessage = `Error al cargar usuarios: ${error.status} ${error.statusText}`;
        }
        
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = this.usuarios;

    // Filtro por búsqueda
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(u =>
        u.nombre.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term) ||
        u.cedula?.toLowerCase().includes(term)
      );
    }

    // Filtro por rol
    if (this.filterRole !== 'ALL') {
      filtered = filtered.filter(u => u.rol === this.filterRole);
    }

    // Filtro por estado
    if (this.filterActivo !== 'ALL') {
      const isActive = this.filterActivo === 'ACTIVO';
      filtered = filtered.filter(u => u.activo === isActive);
    }

    // Ordenar
    filtered.sort((a, b) => {
      const aValue = a[this.sortBy]?.toString().toLowerCase() || '';
      const bValue = b[this.sortBy]?.toString().toLowerCase() || '';
      return aValue.localeCompare(bValue);
    });

    this.usuariosFiltered = filtered;
  }

  openForm(usuario?: Usuario): void {
    if (usuario) {
      this.editingId = usuario.id || null;
      this.formData = { ...usuario };
    } else {
      this.editingId = null;
      this.formData = {
        nombre: '',
        email: '',
        telefono: '',
        cedula: '',
        password: '',
        rol: 'LECTOR',
        activo: true
      };
    }
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editingId = null;
  }

  saveUsuario(): void {
    if (!this.formData.nombre || !this.formData.email) {
      this.errorMessage = 'Nombre y email son requeridos';
      return;
    }

    if (this.editingId) {
      this.usuariosService.updateUsuario(this.editingId, this.formData).subscribe({
        next: () => {
          this.successMessage = '✅ Usuario actualizado correctamente';
          this.closeForm();
          setTimeout(() => this.clearMessages(), 3000);
          this.loadUsuarios();
        },
        error: (error) => {
          console.error('Error actualizando usuario:', error);
          this.errorMessage = error.error?.message || 'Error al actualizar usuario';
        }
      });
    } else {
      this.usuariosService.createUsuario(this.formData).subscribe({
        next: () => {
          this.successMessage = '✅ Usuario creado correctamente';
          this.closeForm();
          setTimeout(() => this.clearMessages(), 3000);
          this.loadUsuarios();
        },
        error: (error) => {
          console.error('Error creando usuario:', error);
          this.errorMessage = error.error?.message || 'Error al crear usuario';
        }
      });
    }
  }

  toggleActivo(usuario: Usuario): void {
    const updated = { ...usuario, activo: !usuario.activo };
    this.usuariosService.updateUsuario(usuario.id!, updated).subscribe({
      next: () => {
        usuario.activo = !usuario.activo;
        this.successMessage = `✅ Usuario ${usuario.activo ? 'activado' : 'desactivado'}`;
        setTimeout(() => this.clearMessages(), 3000);
      },
      error: (error) => {
        console.error('Error actualizando estado:', error);
        this.errorMessage = 'Error al cambiar estado del usuario';
      }
    });
  }

  deleteUsuario(id: number, nombre: string): void {
    if (confirm(`¿Estás seguro de que quieres deshabilitar a ${nombre}?`)) {
      this.usuariosService.deleteUsuario(id).subscribe({
        next: () => {
          this.successMessage = '✅ Usuario deshabilitado correctamente';
          setTimeout(() => this.clearMessages(), 3000);
          this.loadUsuarios();
        },
        error: (error) => {
          console.error('Error eliminando usuario:', error);
          this.errorMessage = error.error?.message || 'Error al deshabilitar usuario';
        }
      });
    }
  }

  onSearch(term: string): void {
    this.searchTerm = term;
    this.applyFilters();
  }

  onFilterRoleChange(event: any): void {
    this.filterRole = event.target.value;
    this.applyFilters();
  }

  onFilterActivoChange(event: any): void {
    this.filterActivo = event.target.value;
    this.applyFilters();
  }

  onSortChange(event: any): void {
    this.sortBy = event.target.value;
    this.applyFilters();
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }
}
