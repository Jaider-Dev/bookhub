import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { BaseApiService } from './base-api.service';

export interface Prestamo {
  id?: number;
  usuarioId?: number;
  usuarioEmail: string;
  ejemplarId?: number;
  libroTitulo: string;
  fechaPrestamo: string;
  fechaVencimiento?: string;
  fechaDevolucionReal?: string | null;
  fechaDevolucion?: string | null; // Alias for fechaDevolucionReal for backward compatibility
  estado: 'ACTIVO' | 'DEVUELTO' | 'VENCIDO';
}

export interface CrearPrestamoRequest {
  usuarioId: number;
  ejemplarId: number;
  fechaDevolucionEsperada?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PrestamosService {
  private readonly API_URL = 'http://localhost:8080/prestamos';
  private readonly CACHE_KEY = 'prestamos';

  constructor(private baseApi: BaseApiService) { }

  /**
   * Obtener todos los préstamos (con caché)
   */
  getPrestamos(): Observable<Prestamo[]> {
    return this.baseApi.get<Prestamo[]>(this.API_URL).pipe(
      tap((prestamos) => {
        // Normalizar fechaDevolucionReal a fechaDevolucion para compatibilidad
        prestamos.forEach(p => {
          if (p.fechaDevolucionReal && !p.fechaDevolucion) {
            p.fechaDevolucion = p.fechaDevolucionReal;
          }
        });
        console.log('[PrestamosService] Préstamos cargados (con caché)');
      })
    );
  }

  /**
   * Obtener préstamo por ID (sin caché)
   */
  getPrestamoById(id: number): Observable<Prestamo> {
    return this.baseApi.getNoCache<Prestamo>(`${this.API_URL}/${id}`);
  }

  /**
   * Crear nuevo préstamo (para ADMIN y LECTOR)
   */
  createPrestamo(prestamo: Prestamo | CrearPrestamoRequest): Observable<Prestamo> {
    return this.baseApi.post<Prestamo>(this.API_URL, prestamo).pipe(
      tap(() => {
        console.log('[PrestamosService] Préstamo creado, invalidando caché');
        this.baseApi.clearCache(this.CACHE_KEY);
      })
    );
  }

  /**
   * Registrar devolución (solo ADMIN)
   */
  devolverPrestamo(id: number): Observable<Prestamo> {
    return this.baseApi.put<Prestamo>(`${this.API_URL}/${id}/devolver`, {}).pipe(
      tap(() => {
        console.log('[PrestamosService] Préstamo devuelto, invalidando caché');
        this.baseApi.clearCache(this.CACHE_KEY);
      })
    );
  }

  /**
   * Actualizar préstamo (para estados, etc.)
   */
  updatePrestamo(id: number, prestamo: Prestamo): Observable<Prestamo> {
    return this.baseApi.put<Prestamo>(`${this.API_URL}/${id}`, prestamo).pipe(
      tap(() => {
        console.log('[PrestamosService] Préstamo actualizado, invalidando caché');
        this.baseApi.clearCache(this.CACHE_KEY);
      })
    );
  }

  /**
   * Obtener préstamos del usuario actual
   */
  getMisPrestamos(): Observable<Prestamo[]> {
    // El backend no expone `/prestamos/mis-prestamos`; obtener todos y filtrar en el cliente
    return this.baseApi.get<Prestamo[]>(`${this.API_URL}`).pipe(
      tap(() => console.log('[PrestamosService] Mis préstamos cargados (desde lista global)'))
    );
  }

  /**
   * Eliminar préstamo (rara vez usado)
   */
  deletePrestamo(id: number): Observable<void> {
    return this.baseApi.delete<void>(`${this.API_URL}/${id}`).pipe(
      tap(() => {
        console.log('[PrestamosService] Préstamo eliminado, invalidando caché');
        this.baseApi.clearCache(this.CACHE_KEY);
      })
    );
  }
}
