import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { Auth } from '../../services/auth';
import { InventarioService, Libro, Ejemplar } from '../../services/inventario.service';
import { PrestamosService, Prestamo, CrearPrestamoRequest } from '../../services/prestamos.service';
import { UsuariosService, Usuario } from '../../services/usuarios.service';
import { UserProfileComponent } from '../user-profile/user-profile.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard-reader',
  standalone: true,
  imports: [CommonModule, RouterModule, UserProfileComponent, FormsModule],
  templateUrl: './dashboard-reader.html',
  styleUrls: ['./dashboard-reader.css']
})
export class DashboardReaderComponent implements OnInit {
  selectedSection: string = 'home';
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Datos para el usuario
  usuarioActual: Usuario | null = null;

  // Datos para inventario (lectura)
  libros: Libro[] = [];
  librosFiltered: Libro[] = [];
  searchLibros = '';

  // Datos para préstamos
  misPrestamos: Prestamo[] = [];
  misPrestamosPendientes: Prestamo[] = [];

  // Formulario para crear préstamo
  showPrestamoForm = false;
  selectedLibroId: number | null = null;
  selectedEjemplarId: number | null = null;
  ejemplaresForSelectedLibro: Ejemplar[] = [];

  constructor(
    public authService: Auth,
    private inventarioService: InventarioService,
    private prestamosService: PrestamosService,
    private usuariosService: UsuariosService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadUserData();
  }

  loadUserData(): void {
    const email = this.authService.getEmailFromToken();
    if (email) {
      this.usuariosService.getUsuarios().subscribe({
        next: (usuarios) => {
          this.usuarioActual = usuarios.find(u => u.email === email) || null;
        },
        error: (error) => {
          console.error('Error cargando usuario:', error);
        }
      });
    }
  }

  selectSection(section: string): void {
    this.selectedSection = section;
    this.clearMessages();

    if (section === 'inventario') {
      this.loadLibros();
    } else if (section === 'mis-prestamos') {
      this.loadMisPrestamos();
    }
  }

  loadLibros(): void {
    this.isLoading = true;
    this.inventarioService.getLibros().subscribe({
      next: (data) => {
        this.libros = data;
        this.applyLibroFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error cargando libros:', error);
        this.errorMessage = 'Error al cargar libros';
        this.isLoading = false;
      }
    });
  }

  applyLibroFilters(): void {
    if (this.searchLibros) {
      const term = this.searchLibros.toLowerCase();
      this.librosFiltered = this.libros.filter(l =>
        l.titulo.toLowerCase().includes(term) ||
        l.autor.toLowerCase().includes(term) ||
        l.categoria.toLowerCase().includes(term)
      );
    } else {
      this.librosFiltered = [...this.libros];
    }
  }

  loadMisPrestamos(): void {
    this.isLoading = true;
    this.prestamosService.getMisPrestamos().subscribe({
      next: (data) => {
        const email = this.authService.getEmailFromToken();
        if (email) {
          this.misPrestamos = data.filter(p => p.usuarioEmail === email);
        } else {
          this.misPrestamos = [];
        }
        this.misPrestamosPendientes = this.misPrestamos.filter(p => p.estado === 'ACTIVO');
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error cargando mis préstamos:', error);
        this.errorMessage = 'Error al cargar mis préstamos';
        this.isLoading = false;
      }
    });
  }

  // Formulario de préstamo
  openPrestamoForm(libroId: number): void {
    this.selectedLibroId = libroId;
    this.selectedEjemplarId = null;
    this.ejemplaresForSelectedLibro = [];
    this.loadEjemplaresForLibro(libroId);
    this.showPrestamoForm = true;
  }

  closePrestamoForm(): void {
    this.showPrestamoForm = false;
    this.selectedLibroId = null;
    this.selectedEjemplarId = null;
    this.ejemplaresForSelectedLibro = [];
  }

  createPrestamo(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.usuarioActual || !this.selectedEjemplarId) {
      this.errorMessage = 'Selecciona un ejemplar disponible antes de solicitar el préstamo.';
      return;
    }

    const request: CrearPrestamoRequest = {
      usuarioId: this.usuarioActual.id!,
      ejemplarId: this.selectedEjemplarId
    };

    this.isLoading = true;
    this.prestamosService.createPrestamo(request).subscribe({
      next: () => {
        this.isLoading = false;
        this.successMessage = '✅ Préstamo registrado correctamente';
        this.closePrestamoForm();
        this.loadMisPrestamos();
        this.loadLibros(); // Recargar libros para actualizar disponibilidad
        setTimeout(() => this.successMessage = '', 5000);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error creando préstamo:', error);
        const errorMsg = typeof error.error === 'string' ? error.error : (error.error?.message || 'Error al crear préstamo');
        this.errorMessage = errorMsg;
        setTimeout(() => this.errorMessage = '', 5000);
      }
    });
  }

  loadEjemplaresForLibro(libroId: number): void {
    this.inventarioService.getEjemplares().subscribe({
      next: (data) => {
        this.ejemplaresForSelectedLibro = data.filter(e => e.libroId === libroId && e.estado === 'DISPONIBLE');
        if (this.ejemplaresForSelectedLibro.length === 0) {
          this.errorMessage = 'No hay ejemplares disponibles para este libro.';
        } else {
          this.errorMessage = '';
        }
      },
      error: (error) => {
        console.error('Error cargando ejemplares:', error);
        this.errorMessage = 'Error al cargar ejemplares';
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }
}