import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router'; // Añadido RouterModule
import { CommonModule } from '@angular/common'; // Módulo para *ngIf
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true, // ¡CLAVE: Es un componente autónomo!
  imports: [CommonModule, RouterModule], // Importa las dependencias del HTML
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class NavbarComponent {

  constructor(public authService: Auth, private router: Router) { }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}