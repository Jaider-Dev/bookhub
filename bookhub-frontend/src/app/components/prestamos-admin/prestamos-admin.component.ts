import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PrestamosService, Prestamo, CrearPrestamoRequest } from '../../services/prestamos.service';

@Component({
  selector: 'app-prestamos-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './prestamos-admin.component.html',
  styleUrls: ['./prestamos-admin.component.css']
})
export class PrestamosAdminComponent implements OnInit {
  prestamos: Prestamo[] = [];
  showForm = false;
  showDevolucionForm = false;
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  activeTab: 'registrar' | 'devolver' = 'registrar';
  prestamoEnDevolucion: Prestamo | null = null;
  
  formData: CrearPrestamoRequest = {
    usuarioId: 0,
    ejemplarId: 0,
    fechaDevolucionEsperada: ''
  };

  constructor(private prestamosService: PrestamosService) {}

  ngOnInit(): void {
    this.loadPrestamos();
  }

  selectTab(tab: 'registrar' | 'devolver'): void {
    this.activeTab = tab;
    this.closeForm();
  }

  loadPrestamos(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.prestamosService.getPrestamos().subscribe({
      next: (data) => {
        this.prestamos = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error cargando préstamos:', error);
        
        if (error.name === 'TimeoutError') {
          this.errorMessage = 'Tiempo de espera agotado. El servidor está tardando. Intenta de nuevo.';
        } else if (error.status === 0) {
          this.errorMessage = 'Error de conexión. Verifica que el servidor esté corriendo en puerto 8080.';
        } else {
          this.errorMessage = `Error al cargar préstamos: ${error.status} ${error.statusText}`;
        }
        
        this.isLoading = false;
      }
    });
  }

  openForm(): void {
    this.formData = {
      usuarioId: 0,
      ejemplarId: 0,
      fechaDevolucionEsperada: ''
    };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.errorMessage = '';
  }

  savePrestamo(): void {
    if (!this.formData.usuarioId || !this.formData.ejemplarId) {
      this.errorMessage = 'Usuario y ejemplar son requeridos';
      return;
    }

    this.prestamosService.createPrestamo(this.formData).subscribe({
      next: () => {
        this.successMessage = 'Préstamo registrado exitosamente';
        this.closeForm();
        this.loadPrestamos();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        console.error('Error creando préstamo:', error);
        this.errorMessage = error.error?.message || 'Error al crear préstamo';
      }
    });
  }

  devolverPrestamo(prestamoId: number, prestamo: Prestamo): void {
    this.prestamoEnDevolucion = prestamo;
    this.showDevolucionForm = true;
  }

  closeDevolucionForm(): void {
    this.showDevolucionForm = false;
    this.prestamoEnDevolucion = null;
  }

  confirmarDevolucion(): void {
    if (!this.prestamoEnDevolucion?.id) return;

    this.prestamosService.devolverPrestamo(this.prestamoEnDevolucion.id).subscribe({
      next: () => {
        this.successMessage = '✅ Devolución registrada exitosamente';
        this.closeDevolucionForm();
        this.loadPrestamos();
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: (error) => {
        console.error('Error registrando devolución:', error);
        this.errorMessage = error.error?.message || 'Error al registrar devolución';
      }
    });
  }
}
