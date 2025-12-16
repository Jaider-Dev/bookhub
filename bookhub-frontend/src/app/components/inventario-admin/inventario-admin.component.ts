import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventarioService, Libro, Autor, Categoria, Ejemplar } from '../../services/inventario.service';

type TabType = 'libros' | 'autores' | 'categorias' | 'ejemplares';

@Component({
  selector: 'app-inventario-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inventario-admin.component.html',
  styleUrls: ['./inventario-admin.component.css']
})
export class InventarioAdminComponent implements OnInit {
  // Tabs
  activeTab: TabType = 'libros';

  // Estado General
  isLoading = false;
  errorMessage = '';
  successMessage = '';
  showForm = false;
  editingId: number | null = null;

  // LIBROS
  libros: Libro[] = [];
  librosFiltered: Libro[] = [];
  searchLibros = '';
  sortByLibros: 'titulo' | 'autor' | 'categoria' = 'titulo';
  formLibro: Libro = { titulo: '', autor: '', categoria: '', ejemplares: 0 };

  // AUTORES
  autores: Autor[] = [];
  autoresFiltered: Autor[] = [];
  searchAutores = '';
  formAutor: Autor = { nombre: '' };

  // CATEGORIAS
  categorias: Categoria[] = [];
  categoriasFiltered: Categoria[] = [];
  searchCategorias = '';
  formCategoria: Categoria = { nombre: '' };

  // EJEMPLARES
  ejemplares: Ejemplar[] = [];
  ejemplaresFiltered: Ejemplar[] = [];
  searchEjemplares = '';
  sortByEjemplares: 'libroTitulo' | 'estado' = 'libroTitulo';
  formEjemplar: Ejemplar = { libroId: 0, estado: 'DISPONIBLE' };

  constructor(private inventarioService: InventarioService) { }

  ngOnInit(): void {
    this.loadAllData();
  }

  loadAllData(): void {
    this.isLoading = true;
    this.errorMessage = '';
    console.log('[InventarioAdmin] Cargando todos los datos...');

    // Cargar libros
    this.inventarioService.getLibros().subscribe({
      next: (data) => {
        console.log('[InventarioAdmin] Libros cargados:', data.length);
        this.libros = data;
        this.applyLibrosFilters();
      },
      error: (error) => {
        console.error('[InventarioAdmin] Error cargando libros:', error);
        this.errorMessage = 'Error al cargar libros';
      }
    });

    // Cargar autores
    this.inventarioService.getAutores().subscribe({
      next: (data) => {
        console.log('[InventarioAdmin] Autores cargados:', data.length);
        this.autores = data;
        this.applyAutoresFilters();
      },
      error: (error) => {
        console.error('[InventarioAdmin] Error cargando autores:', error);
      }
    });

    // Cargar categorías
    this.inventarioService.getCategorias().subscribe({
      next: (data) => {
        console.log('[InventarioAdmin] Categorías cargadas:', data.length);
        this.categorias = data;
        this.applyCategoriasFilters();
      },
      error: (error) => {
        console.error('[InventarioAdmin] Error cargando categorías:', error);
      }
    });

    // Cargar ejemplares
    this.inventarioService.getEjemplares().subscribe({
      next: (data) => {
        console.log('[InventarioAdmin] Ejemplares cargados:', data.length);
        this.ejemplares = data;
        this.applyEjemplaresFilters();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('[InventarioAdmin] Error cargando ejemplares:', error);
        this.isLoading = false;
      }
    });
  }

  // ==================== LIBROS ====================

  applyLibrosFilters(): void {
    let filtered = this.libros;

    if (this.searchLibros) {
      const term = this.searchLibros.toLowerCase();
      filtered = filtered.filter(l =>
        l.titulo.toLowerCase().includes(term) ||
        l.autor.toLowerCase().includes(term) ||
        l.categoria.toLowerCase().includes(term)
      );
    }

    filtered.sort((a, b) => {
      const aVal = a[this.sortByLibros].toString().toLowerCase();
      const bVal = b[this.sortByLibros].toString().toLowerCase();
      return aVal.localeCompare(bVal);
    });

    this.librosFiltered = filtered;
  }

  openFormLibro(libro?: Libro): void {
    if (libro) {
      this.editingId = libro.id || null;
      this.formLibro = { ...libro };
    } else {
      this.editingId = null;
      this.formLibro = { titulo: '', autor: '', categoria: '', ejemplares: 0 };
    }
    this.activeTab = 'libros';
    this.showForm = true;
  }

  saveLibro(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.formLibro.titulo || !this.formLibro.autor || !this.formLibro.categoria) {
      this.errorMessage = 'Título, autor y categoría son requeridos';
      return;
    }

    if (this.editingId) {
      this.inventarioService.updateLibro(this.editingId, this.formLibro).subscribe({
        next: () => {
          this.successMessage = '✅ Libro actualizado correctamente';
          this.closeForm();
          this.loadAllData();
          setTimeout(() => this.clearMessages(), 3000);
        },
        error: (error) => {
          console.error('Error actualizando libro:', error);
          const errorMsg = typeof error.error === 'string' ? error.error : (error.error?.message || 'Error al actualizar libro');
          this.errorMessage = errorMsg;
          setTimeout(() => this.errorMessage = '', 5000);
        }
      });
    } else {
      this.inventarioService.createLibro(this.formLibro).subscribe({
        next: () => {
          this.successMessage = '✅ Libro creado correctamente';
          this.closeForm();
          this.loadAllData();
          setTimeout(() => this.clearMessages(), 3000);
        },
        error: (error) => {
          console.error('Error creando libro:', error);
          const errorMsg = typeof error.error === 'string' ? error.error : (error.error?.message || 'Error al crear libro');
          this.errorMessage = errorMsg;
          setTimeout(() => this.errorMessage = '', 5000);
        }
      });
    }
  }

  deleteLibro(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este libro?')) {
      this.inventarioService.deleteLibro(id).subscribe({
        next: () => {
          this.successMessage = 'Libro eliminado correctamente';
          this.loadAllData();
        },
        error: (error) => {
          console.error('Error eliminando libro:', error);
          this.errorMessage = 'Error al eliminar libro';
        }
      });
    }
  }

  // ==================== AUTORES ====================

  applyAutoresFilters(): void {
    let filtered = this.autores;

    if (this.searchAutores) {
      const term = this.searchAutores.toLowerCase();
      filtered = filtered.filter(a => a.nombre.toLowerCase().includes(term));
    }

    filtered.sort((a, b) => a.nombre.localeCompare(b.nombre));
    this.autoresFiltered = filtered;
  }

  openFormAutor(autor?: Autor): void {
    if (autor) {
      this.editingId = autor.id || null;
      this.formAutor = { ...autor };
    } else {
      this.editingId = null;
      this.formAutor = { nombre: '' };
    }
    this.activeTab = 'autores';
    this.showForm = true;
  }

  saveAutor(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.formAutor.nombre) {
      this.errorMessage = 'El nombre del autor es requerido';
      setTimeout(() => this.errorMessage = '', 5000);
      return;
    }

    if (this.editingId) {
      this.inventarioService.updateAutor(this.editingId, this.formAutor).subscribe({
        next: () => {
          this.successMessage = '✅ Autor actualizado correctamente';
          this.closeForm();
          this.loadAllData();
          setTimeout(() => this.clearMessages(), 3000);
        },
        error: (error) => {
          console.error('Error actualizando autor:', error);
          this.errorMessage = error.error?.message || 'Error al actualizar autor';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      });
    } else {
      this.inventarioService.createAutor(this.formAutor).subscribe({
        next: () => {
          this.successMessage = '✅ Autor creado correctamente';
          this.closeForm();
          this.loadAllData();
          setTimeout(() => this.clearMessages(), 3000);
        },
        error: (error) => {
          console.error('Error creando autor:', error);
          this.errorMessage = error.error?.message || 'Error al crear autor';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      });
    }
  }

  deleteAutor(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este autor?')) {
      this.inventarioService.deleteAutor(id).subscribe({
        next: () => {
          this.successMessage = 'Autor eliminado correctamente';
          this.loadAllData();
        },
        error: (error) => {
          console.error('Error eliminando autor:', error);
          this.errorMessage = 'Error al eliminar autor';
        }
      });
    }
  }

  // ==================== CATEGORIAS ====================

  applyCategoriasFilters(): void {
    let filtered = this.categorias;

    if (this.searchCategorias) {
      const term = this.searchCategorias.toLowerCase();
      filtered = filtered.filter(c => c.nombre.toLowerCase().includes(term));
    }

    filtered.sort((a, b) => a.nombre.localeCompare(b.nombre));
    this.categoriasFiltered = filtered;
  }

  openFormCategoria(categoria?: Categoria): void {
    if (categoria) {
      this.editingId = categoria.id || null;
      this.formCategoria = { ...categoria };
    } else {
      this.editingId = null;
      this.formCategoria = { nombre: '' };
    }
    this.activeTab = 'categorias';
    this.showForm = true;
  }

  saveCategoria(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.formCategoria.nombre) {
      this.errorMessage = 'El nombre de la categoría es requerido';
      setTimeout(() => this.errorMessage = '', 5000);
      return;
    }

    if (this.editingId) {
      this.inventarioService.updateCategoria(this.editingId, this.formCategoria).subscribe({
        next: () => {
          this.successMessage = '✅ Categoría actualizada correctamente';
          this.closeForm();
          this.loadAllData();
          setTimeout(() => this.clearMessages(), 3000);
        },
        error: (error) => {
          console.error('Error actualizando categoría:', error);
          this.errorMessage = error.error?.message || 'Error al actualizar categoría';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      });
    } else {
      this.inventarioService.createCategoria(this.formCategoria).subscribe({
        next: () => {
          this.successMessage = '✅ Categoría creada correctamente';
          this.closeForm();
          this.loadAllData();
          setTimeout(() => this.clearMessages(), 3000);
        },
        error: (error) => {
          console.error('Error creando categoría:', error);
          this.errorMessage = error.error?.message || 'Error al crear categoría';
          setTimeout(() => this.errorMessage = '', 5000);
        }
      });
    }
  }

  deleteCategoria(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar esta categoría?')) {
      this.inventarioService.deleteCategoria(id).subscribe({
        next: () => {
          this.successMessage = 'Categoría eliminada correctamente';
          this.loadAllData();
        },
        error: (error) => {
          console.error('Error eliminando categoría:', error);
          this.errorMessage = 'Error al eliminar categoría';
        }
      });
    }
  }

  // ==================== EJEMPLARES ====================

  applyEjemplaresFilters(): void {
    let filtered = this.ejemplares;

    if (this.searchEjemplares) {
      const term = this.searchEjemplares.toLowerCase();
      filtered = filtered.filter(e => e.libroId.toString().includes(term));
    }

    if (this.sortByEjemplares) {
      filtered.sort((a, b) => {
        const aVal = a[this.sortByEjemplares as keyof Ejemplar]?.toString().toLowerCase() || '';
        const bVal = b[this.sortByEjemplares as keyof Ejemplar]?.toString().toLowerCase() || '';
        return aVal.localeCompare(bVal);
      });
    }

    this.ejemplaresFiltered = filtered;
  }

  openFormEjemplar(ejemplar?: Ejemplar): void {
    if (ejemplar) {
      this.editingId = ejemplar.id || null;
      this.formEjemplar = { ...ejemplar };
    } else {
      this.editingId = null;
      this.formEjemplar = { libroId: 0, estado: 'DISPONIBLE' };
    }
    this.activeTab = 'ejemplares';
    this.showForm = true;
  }

  saveEjemplar(): void {
    if (!this.formEjemplar.libroId) {
      this.errorMessage = 'El ID del libro es requerido';
      return;
    }

    if (this.editingId) {
      this.inventarioService.updateEjemplar(this.editingId, this.formEjemplar).subscribe({
        next: () => {
          this.successMessage = 'Ejemplar actualizado correctamente';
          this.closeForm();
          this.loadAllData();
        },
        error: (error) => {
          console.error('Error actualizando ejemplar:', error);
          this.errorMessage = 'Error al actualizar ejemplar';
        }
      });
    } else {
      this.inventarioService.createEjemplar(this.formEjemplar).subscribe({
        next: () => {
          this.successMessage = 'Ejemplar creado correctamente';
          this.closeForm();
          this.loadAllData();
        },
        error: (error) => {
          console.error('Error creando ejemplar:', error);
          this.errorMessage = 'Error al crear ejemplar';
        }
      });
    }
  }

  deleteEjemplar(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este ejemplar?')) {
      this.inventarioService.deleteEjemplar(id).subscribe({
        next: () => {
          this.successMessage = 'Ejemplar eliminado correctamente';
          this.loadAllData();
        },
        error: (error) => {
          console.error('Error eliminando ejemplar:', error);
          this.errorMessage = 'Error al eliminar ejemplar';
        }
      });
    }
  }

  // ==================== COMÚN ====================

  selectTab(tab: TabType): void {
    this.activeTab = tab;
    this.showForm = false;
    this.editingId = null;
  }

  closeForm(): void {
    this.showForm = false;
    this.editingId = null;
    this.clearMessages();
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }
}
