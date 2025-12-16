import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private readonly API_URL = 'http://localhost:8080/usuarios';

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: any): Observable<any> {
    const loginUrl = `${this.API_URL}/login`;
    return this.http.post<any>(loginUrl, credentials).pipe(
      tap(response => {
        if (response && response.token) {
          this.setSession(response.token);
        }
      }),
      catchError(error => {
        // No logout on login error, just throw
        throw error;
      })
    );
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    this.router.navigate(['/login']);
  }

  private setSession(token: string): void {
    localStorage.setItem('jwt_token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token;
  }

  getRoleFromToken(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        return decodedToken.rol || decodedToken.role || null;
      } catch (error) {
        console.error('Error decodificando el token:', error);
        return null;
      }
    }
    return null;
  }

  getEmailFromToken(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        return decodedToken.email || decodedToken.sub || null;
      } catch (error) {
        console.error('Error decodificando el token:', error);
        return null;
      }
    }
    return null;
  }

  isAdmin(): boolean {
    const role = this.getRoleFromToken();
    return role === 'ADMIN';
  }
}