## 2. API REST y CRUD de microservicios

### 2.1 Microservicio `service-usuarios` (puerto 8081)

- **Entidad principal**: `Usuario`.
- **Ejemplos de endpoints** (vía gateway, prefijo `/usuarios`):
  - `POST /usuarios` – crear usuario (admin).
  - `GET /usuarios` – listar usuarios.
  - `GET /usuarios/{id}` – obtener usuario por id.
  - `PUT /usuarios/{id}` – actualizar usuario.
  - `DELETE /usuarios/{id}` – eliminar usuario.
  - `POST /usuarios/login` – autenticación y obtención de JWT.

### 2.2 Microservicio `service-inventario` (puerto 8082)

- **Entidad principal**: `Libro`.
- **Ejemplos de endpoints** (vía gateway, prefijo `/inventario`):
  - `POST /inventario/libros` – crear libro.
  - `GET /inventario/libros` – listar libros.
  - `GET /inventario/libros/{id}` – obtener libro.
  - `PUT /inventario/libros/{id}` – actualizar libro.
  - `DELETE /inventario/libros/{id}` – eliminar libro.

### 2.3 Microservicio `service-prestamos` (puerto 8083)

- **Entidad principal**: `Prestamo`.
- **Ejemplos de endpoints** (vía gateway, prefijo `/prestamos`):
  - `POST /prestamos` – crear préstamo.
  - `GET /prestamos` – listar préstamos.
  - `GET /prestamos/{id}` – obtener préstamo.
  - `PUT /prestamos/{id}` – actualizar préstamo.
  - `PUT /prestamos/{id}/devolver` – marcar un préstamo como devuelto.



