import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { Auth } from '../services/auth'; 

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  
  constructor(private authService: Auth, private router: Router) {}

  canActivate(): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    if (this.authService.isAdmin()) {
      return true;
    } else {
      alert('Acceso denegado: Se requiere rol ADMIN.');
      this.router.navigate(['/reader']); 
      return false;
    }
  }
}