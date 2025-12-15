import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { BaseApiService } from './base-api.service';

export interface Usuario {
  id?: number;
  nombre: string;
  email: string;
  telefono?: string | null;
  cedula?: string | null;
  password?: string;
  activo: boolean;
  rol: 'ADMIN' | 'LECTOR';
}

@Injectable({
  providedIn: 'root'
})
export class UsuariosService {
  // Route requests through the gateway to use centralized CORS and auth
  private readonly API_URL = 'http://localhost:8080/usuarios';

  constructor(private baseApi: BaseApiService) { }

  /**
   * Obtener todos los usuarios (con caché)
   */
  getUsuarios(): Observable<Usuario[]> {
    return this.baseApi.get<Usuario[]>(this.API_URL).pipe(
      tap(() => console.log('[UsuariosService] Usuarios cargados (con caché)'))
    );
  }

  /**
   * Obtener usuario por ID (sin caché)
   */
  getUsuarioById(id: number): Observable<Usuario> {
    return this.baseApi.getNoCache<Usuario>(`${this.API_URL}/${id}`);
  }

  /**
   * Crear nuevo usuario
   */
  createUsuario(usuario: Usuario): Observable<Usuario> {
    return this.baseApi.post<Usuario>(this.API_URL, usuario).pipe(
      tap(() => {
        console.log('[UsuariosService] Usuario creado, invalidando caché');
        this.baseApi.clearCacheForUrl(this.API_URL);
      })
    );
  }

  /**
   * Actualizar usuario existente
   */
  updateUsuario(id: number, usuario: Usuario): Observable<Usuario> {
    return this.baseApi.put<Usuario>(`${this.API_URL}/${id}`, usuario).pipe(
      tap(() => {
        console.log('[UsuariosService] Usuario actualizado, invalidando caché');
        this.baseApi.clearCacheForUrl(this.API_URL);
      })
    );
  }

  /**
   * Eliminar (deshabilitar) usuario
   */
  deleteUsuario(id: number): Observable<void> {
    return this.baseApi.delete<void>(`${this.API_URL}/${id}`).pipe(
      tap(() => {
        console.log('[UsuariosService] Usuario eliminado, invalidando caché');
        this.baseApi.clearCacheForUrl(this.API_URL);
      })
    );
  }

  /**
   * Cambiar contraseña del usuario actual
   */
  changePassword(usuarioId: number, currentPassword: string, newPassword: string): Observable<any> {
    // El servicio de usuarios expone PATCH /usuarios/{id}/Password con el campo 'nuevaPassword'
    return this.baseApi.patch(`${this.API_URL}/${usuarioId}/Password`, {
      nuevaPassword: newPassword
    });
  }
}
