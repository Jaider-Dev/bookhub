import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { BaseApiService } from './base-api.service';

export interface Libro {
  id?: number;
  titulo: string;
  autor: string;
  categoria: string;
  ejemplares: number;
}

export interface Autor {
  id?: number;
  nombre: string;
}

export interface Categoria {
  id?: number;
  nombre: string;
}

export interface Ejemplar {
  id?: number;
  libroId: number;
  codigoInventario?: string;
  estado: 'DISPONIBLE' | 'PRESTADO' | 'REPARACION';
}

@Injectable({
  providedIn: 'root'
})
export class InventarioService {
  private readonly ROOT = 'http://localhost:8080';
  private readonly API_URL = `${this.ROOT}/inventario`;
  private readonly CACHE_KEY = 'inventario';

  constructor(private baseApi: BaseApiService) { }

  /**
   * Obtener todos los libros (con caché)
   */
  getLibros(): Observable<Libro[]> {
    // Primero obtener libros, autores, categorías y ejemplares
    return new Observable(observer => {
      this.baseApi.get<any[]>(`${this.API_URL}/libros`).subscribe({
        next: (librosRaw: any[]) => {
          // Obtener autores y categorías para transformar
          this.baseApi.get<Autor[]>(`${this.API_URL}/autores`).subscribe({
            next: (autores: Autor[]) => {
              this.baseApi.get<Categoria[]>(`${this.API_URL}/categorias`).subscribe({
                next: (categorias: Categoria[]) => {
                  this.baseApi.get<Ejemplar[]>(`${this.API_URL}/ejemplares`).subscribe({
                    next: (ejemplares: Ejemplar[]) => {
                      // Transformar libros
                      const librosTransformados: Libro[] = librosRaw.map((libro: any) => {
                        const autor = autores.find(a => a.id === libro.autorId);
                        const categoria = categorias.find(c => c.id === libro.categoriaId);
                        const ejemplaresDelLibro = ejemplares.filter(e => e.libroId === libro.id && e.estado === 'DISPONIBLE');

                        return {
                          id: libro.id,
                          titulo: libro.titulo,
                          autor: autor?.nombre || 'Desconocido',
                          categoria: categoria?.nombre || 'Sin categoría',
                          ejemplares: ejemplaresDelLibro.length
                        };
                      });
                      observer.next(librosTransformados);
                      observer.complete();
                    },
                    error: (err) => observer.error(err)
                  });
                },
                error: (err) => observer.error(err)
              });
            },
            error: (err) => observer.error(err)
          });
        },
        error: (err) => observer.error(err)
      });
    });
  }

  /**
   * Obtener libro por ID (sin caché)
   */
  getLibroById(id: number): Observable<Libro> {
    return this.baseApi.getNoCache<Libro>(`${this.API_URL}/libros/${id}`);
  }

  /**
   * Crear nuevo libro
   */
  createLibro(libro: Libro): Observable<Libro> {
    return this.baseApi.post<Libro>(`${this.API_URL}/libros`, libro).pipe(
      tap(() => {
        console.log('[InventarioService] Libro creado, invalidando caché');
        this.baseApi.clearCache(this.CACHE_KEY);
      })
    );
  }

  /**
   * Actualizar libro existente
   */
  updateLibro(id: number, libro: Libro): Observable<Libro> {
    return this.baseApi.put<Libro>(`${this.API_URL}/libros/${id}`, libro).pipe(
      tap(() => {
        console.log('[InventarioService] Libro actualizado, invalidando caché');
        this.baseApi.clearCache(this.CACHE_KEY);
      })
    );
  }

  /**
   * Eliminar libro
   */
  deleteLibro(id: number): Observable<void> {
    return this.baseApi.delete<void>(`${this.API_URL}/libros/${id}`).pipe(
      tap(() => {
        console.log('[InventarioService] Libro eliminado, invalidando caché');
        this.baseApi.clearCache(this.CACHE_KEY);
      })
    );
  }

  /**
   * Obtener todos los autores (con caché)
   */
  getAutores(): Observable<Autor[]> {
    return this.baseApi.get<Autor[]>(`${this.API_URL}/autores`).pipe(
      tap(() => console.log('[InventarioService] Autores cargados (con caché)'))
    );
  }

  /**
   * Crear nuevo autor
   */
  createAutor(autor: Autor): Observable<Autor> {
    return this.baseApi.post<Autor>(`${this.API_URL}/autores`, autor).pipe(
      tap(() => this.baseApi.clearCache(this.CACHE_KEY))
    );
  }

  /**
   * Actualizar autor
   */
  updateAutor(id: number, autor: Autor): Observable<Autor> {
    return this.baseApi.put<Autor>(`${this.API_URL}/autores/${id}`, autor).pipe(
      tap(() => this.baseApi.clearCache(this.CACHE_KEY))
    );
  }

  /**
   * Eliminar autor
   */
  deleteAutor(id: number): Observable<void> {
    return this.baseApi.delete<void>(`${this.API_URL}/autores/${id}`).pipe(
      tap(() => this.baseApi.clearCache(this.CACHE_KEY))
    );
  }

  /**
   * Obtener todas las categorías (con caché)
   */
  getCategorias(): Observable<Categoria[]> {
    return this.baseApi.get<Categoria[]>(`${this.API_URL}/categorias`).pipe(
      tap(() => console.log('[InventarioService] Categorías cargadas (con caché)'))
    );
  }

  /**
   * Crear nueva categoría
   */
  createCategoria(categoria: Categoria): Observable<Categoria> {
    return this.baseApi.post<Categoria>(`${this.API_URL}/categorias`, categoria).pipe(
      tap(() => this.baseApi.clearCache(this.CACHE_KEY))
    );
  }

  /**
   * Actualizar categoría
   */
  updateCategoria(id: number, categoria: Categoria): Observable<Categoria> {
    return this.baseApi.put<Categoria>(`${this.API_URL}/categorias/${id}`, categoria).pipe(
      tap(() => this.baseApi.clearCache(this.CACHE_KEY))
    );
  }

  /**
   * Eliminar categoría
   */
  deleteCategoria(id: number): Observable<void> {
    return this.baseApi.delete<void>(`${this.API_URL}/categorias/${id}`).pipe(
      tap(() => this.baseApi.clearCache(this.CACHE_KEY))
    );
  }

  /**
   * Obtener todos los ejemplares
   */
  getEjemplares(): Observable<Ejemplar[]> {
    return this.baseApi.get<Ejemplar[]>(`${this.API_URL}/ejemplares`).pipe(
      tap(() => console.log('[InventarioService] Ejemplares cargados (con caché)'))
    );
  }

  /**
   * Crear nuevo ejemplar
   */
  createEjemplar(ejemplar: Ejemplar): Observable<Ejemplar> {
    return this.baseApi.post<Ejemplar>(`${this.API_URL}/ejemplares`, ejemplar).pipe(
      tap(() => this.baseApi.clearCache(this.CACHE_KEY))
    );
  }

  /**
   * Actualizar ejemplar
   */
  updateEjemplar(id: number, ejemplar: Ejemplar): Observable<Ejemplar> {
    return this.baseApi.put<Ejemplar>(`${this.API_URL}/ejemplares/${id}`, ejemplar).pipe(
      tap(() => this.baseApi.clearCache(this.CACHE_KEY))
    );
  }

  /**
   * Eliminar ejemplar
   */
  deleteEjemplar(id: number): Observable<void> {
    return this.baseApi.delete<void>(`${this.API_URL}/ejemplares/${id}`).pipe(
      tap(() => this.baseApi.clearCache(this.CACHE_KEY))
    );
  }
}
