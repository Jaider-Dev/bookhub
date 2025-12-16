# 3. Diseño de base de datos y entidades JPA

El sistema utiliza una bases de datos MySQL, organizada lógicamente en esquemas separados por microservicio para garantizar el desacoplamiento.

## Estructura de Tablas (Cumplimiento de Requisitos)

Se cumple con el requisito de "Mínimo 4 tablas, dos con llaves foráneas":

1.  **Tabla A (Principal/Independiente):** `usuarios` (Esquema `bookhub_usuarios`).
    *   Gestiona la información de acceso y perfil.
2.  **Tabla B (Principal/Maestra):** `libro` (Esquema `bookhub_inventario`).
    *   Contiene la información bibliográfica (Título, ISBN, Año).
    *   **Llaves Foráneas:**
        *   `autor_id` -> Referencia a tabla `autor`.
        *   `categoria_id` -> Referencia a tabla `categoria`.
3.  **Tabla C (Transaccional/Relación):** `prestamo` (Esquema `bookhub_prestamos`).
    *   Registra la acción de prestar un ejemplar a un usuario.
    *   **Relaciones Lógicas (Microservicios):**
        *   `usuario_id` -> ID lógico del usuario.
        *   `ejemplar_id` -> ID lógico del ejemplar.
4.  **Tabla D (Detalle/Dependiente):** `ejemplar` (Esquema `bookhub_inventario`).
    *   Representa la unidad física del libro.
    *   **Llaves Foráneas:**
        *   `libro_id` -> Referencia a tabla `libro` (Constraint SQL explícito).

## Relaciones JPA Implementadas

*   **@ManyToOne / @OneToMany**:
    *   `Libro` <-> `Autor` (`@ManyToOne` en Libro).
    *   `Libro` <-> `Categoria` (`@ManyToOne` en Libro).
    *   `Ejemplar` <-> `Libro` (`@ManyToOne` en Ejemplar).
*   **Persistencia**:
    *   Se utiliza `Spring Data JPA` con repositorios (`JpaRepository`) para todas las operaciones CRUD.
    *   Inicialización de esquema y datos mediante `init.sql` para integridad y consistencia en el despliegue Docker.
