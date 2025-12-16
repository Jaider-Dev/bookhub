## 4. Docker y despliegue con docker-compose

### 4.1 Imagen Docker para cada microservicio (Spring Boot)

Cada microservicio (`bookhub-gateway`, `service-usuarios`, `service-inventario`, `service-prestamos`) utiliza un `Dockerfile` multi-stage:

- **Stage 1 (build)**: usa `maven:3.9-eclipse-temurin-17` para compilar el proyecto y generar el `.jar`.
- **Stage 2 (runtime)**: usa `eclipse-temurin:17-jre` para ejecutar el `.jar` resultante.

Las propiedades de conexión a base de datos se parametrizan con variables de entorno (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).

### 4.2 Imagen Docker para el frontend Angular

El frontend `bookhub-frontend` también usa un `Dockerfile` multi-stage:

- **Stage 1**: `node:20-alpine` para instalar dependencias y ejecutar `npm run build`.
- **Stage 2**: `nginx:alpine` para servir los archivos estáticos.

### 4.3 Orquestación con `docker-compose.yml`

El archivo `docker-compose.yml` en la raíz del proyecto define los servicios:

- `mysql`: base de datos MySQL con las bases `bookhub_usuarios`, `bookhub_inventario`, `bookhub_prestamos`.
- `bookhub-gateway`: API Gateway en el puerto 8080.
- `service-usuarios`: microservicio de usuarios en el puerto 8081.
- `service-inventario`: microservicio de inventario en el puerto 8082.
- `service-prestamos`: microservicio de préstamos en el puerto 8083.
- `bookhub-frontend`: frontend Angular servido por Nginx en el puerto 4200.

Los servicios backend dependen de `mysql` y se conectan a él usando el hostname `mysql` (no `localhost`).

### 4.4 Ejecución

Desde la raíz del proyecto:

```bash
docker compose up -d
```

Esto construirá las imágenes necesarias (si no existen) y levantará:

- Frontend: `http://localhost:4200`
- Gateway: `http://localhost:8080`



