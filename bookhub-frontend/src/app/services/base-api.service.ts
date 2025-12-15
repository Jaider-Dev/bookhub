import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { timeout, retry, shareReplay } from 'rxjs/operators';

/**
 * Servicio base para todas las llamadas HTTP
 * Proporciona:
 * - Manejo centralizado de timeouts
 * - Caché de GET requests
 * - Manejo de errores consistente
 * - Reintentos automáticos
 */
@Injectable({
  providedIn: 'root'
})
export class BaseApiService {
  private readonly TIMEOUT_MS = 8000; // 8 segundos (mejorado de 10s)
  private readonly RETRY_COUNT = 1;
  private readonly RETRY_DELAY_MS = 300; // Reducido de 500ms
  private cache = new Map<string, Observable<any>>();

  constructor(private http: HttpClient) {}

  /**
   * GET con caché automático
   */
  get<T>(url: string, useCache = true): Observable<T> {
    if (useCache && this.cache.has(url)) {
      return this.cache.get(url)!;
    }

    const request$ = this.http.get<T>(url).pipe(
      timeout(this.TIMEOUT_MS),
      retry({ count: this.RETRY_COUNT, delay: this.RETRY_DELAY_MS }),
      shareReplay(1)
    );

    if (useCache) {
      this.cache.set(url, request$);
    }

    return request$;
  }

  /**
   * GET sin caché (para datos que cambian frecuentemente)
   */
  getNoCache<T>(url: string): Observable<T> {
    return this.http.get<T>(url).pipe(
      timeout(this.TIMEOUT_MS),
      retry({ count: this.RETRY_COUNT, delay: this.RETRY_DELAY_MS })
    );
  }

  /**
   * POST con timeout
   */
  post<T>(url: string, body: any): Observable<T> {
    return this.http.post<T>(url, body).pipe(
      timeout(this.TIMEOUT_MS)
    );
  }

  /**
   * PUT con timeout
   */
  put<T>(url: string, body: any): Observable<T> {
    return this.http.put<T>(url, body).pipe(
      timeout(this.TIMEOUT_MS)
    );
  }

  /**
   * PATCH con timeout
   */
  patch<T>(url: string, body: any): Observable<T> {
    return this.http.patch<T>(url, body).pipe(
      timeout(this.TIMEOUT_MS)
    );
  }

  /**
   * DELETE con timeout
   */
  delete<T>(url: string): Observable<T> {
    return this.http.delete<T>(url).pipe(
      timeout(this.TIMEOUT_MS)
    );
  }

  /**
   * Limpiar caché (llamar después de POST, PUT, DELETE)
   */
  clearCache(pattern?: string): void {
    if (pattern) {
      Array.from(this.cache.keys())
        .filter(key => key.includes(pattern))
        .forEach(key => this.cache.delete(key));
    } else {
      this.cache.clear();
    }
  }

  /**
   * Limpiar una URL específica del caché
   */
  clearCacheForUrl(url: string): void {
    this.cache.delete(url);
  }
}
