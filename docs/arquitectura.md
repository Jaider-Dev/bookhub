## 5. Patrón de microservicio utilizado

- **API Gateway**: implementado con `bookhub-gateway` (Spring Cloud Gateway).
- **Microservicios independientes**:
  - `service-usuarios` (autenticación y gestión de usuarios).
  - `service-inventario` (gestión de libros / inventario).
  - `service-prestamos` (gestión de préstamos y devoluciones).

Opcionalmente se puede documentar:

- **Service Registry (Eureka)**: no implementado actualmente.
- **Circuit Breaker / Resilience4j**: no implementado actualmente.

## 6. Arquitectura general

Componentes principales:

- **Frontend Angular** (`bookhub-frontend`): interfaz para administradores y lectores.
- **API Gateway** (`bookhub-gateway`): punto de entrada único a los microservicios.
- **Microservicios**:
  - `service-usuarios` en puerto 8081.
  - `service-inventario` en puerto 8082.
  - `service-prestamos` en puerto 8083.
- **Base de datos MySQL**: contenedor Docker con varias bases de datos lógicas (`bookhub_usuarios`, `bookhub_inventario`, `bookhub_prestamos`).

Flujo básico:

1. El usuario accede al **frontend Angular**.
2. El frontend llama al **API Gateway** en el puerto 8080.
3. El gateway enruta las peticiones a:
   - `/usuarios/**` → `service-usuarios` (8081)
   - `/inventario/**` → `service-inventario` (8082)
   - `/prestamos/**` → `service-prestamos` (8083)
4. Cada microservicio accede a la base de datos MySQL mediante JPA.



