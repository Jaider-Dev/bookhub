import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { Auth } from '../../services/auth';
import { UsuariosAdminComponent } from '../usuarios-admin/usuarios-admin.component';
import { InventarioAdminComponent } from '../inventario-admin/inventario-admin.component';
import { PrestamosAdminComponent } from '../prestamos-admin/prestamos-admin.component';
import { UserProfileComponent } from '../user-profile/user-profile.component';
import { UsuariosService } from '../../services/usuarios.service';
import { InventarioService } from '../../services/inventario.service';
import { PrestamosService } from '../../services/prestamos.service';

@Component({
  selector: 'app-dashboard-admin',
  standalone: true,
  imports: [CommonModule, RouterModule, UsuariosAdminComponent, InventarioAdminComponent, PrestamosAdminComponent, UserProfileComponent],
  templateUrl: './dashboard-admin.html',
  styleUrls: ['./dashboard-admin.css']
})
export class DashboardAdminComponent implements OnInit {
  selectedSection: string = 'home';
  totalUsuarios = 0;
  totalLibros = 0;
  totalPrestamos = 0;

  constructor(
    public authService: Auth, 
    private router: Router,
    private usuariosService: UsuariosService,
    private inventarioService: InventarioService,
    private prestamosService: PrestamosService
  ) {}

  ngOnInit(): void {
    this.loadStats();
  }

  loadStats(): void {
    // Cargar estadísticas
    this.usuariosService.getUsuarios().subscribe({
      next: (usuarios) => {
        this.totalUsuarios = usuarios.length;
      },
      error: () => {
        this.totalUsuarios = 0;
      }
    });

    this.inventarioService.getLibros().subscribe({
      next: (libros) => {
        this.totalLibros = libros.length;
      },
      error: () => {
        this.totalLibros = 0;
      }
    });

    this.prestamosService.getPrestamos().subscribe({
      next: (prestamos) => {
        this.totalPrestamos = prestamos.filter(p => p.estado === 'ACTIVO').length;
      },
      error: () => {
        this.totalPrestamos = 0;
      }
    });
  }

  selectSection(section: string): void {
    this.selectedSection = section;
    // Si volvemos a Inicio, recargar estadísticas para reflejar cambios recientes
    if (section === 'home') {
      // Pequeña espera para asegurar que interceptores/token estén listos
      setTimeout(() => this.loadStats(), 50);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}