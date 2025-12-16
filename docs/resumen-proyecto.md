# INFORME FINAL INTEGRADO - BOOKHUB
**Programa:** Ingeniería de Sistemas  
**Materia:** Desarrollo de Software III  
**Docente:** Mg(c). Juan Pablo Pinillos Reina  
**Estudiante:** Jaider Bermudez Giron

---

# 1. Descripción del Problema
La gestión monolítica y manual de bibliotecas presenta desafíos de concurrencia y consistencia de datos. Los sistemas tradicionales centralizados sufren de **Single Point of Failure (SPOF)** y acoplamiento fuerte, lo que dificulta la mantenibilidad evolutiva. **BookHub** nace para resolver la necesidad de un sistema distribuido, tolerante a fallos y accesible vía web.

# 2. Objetivo General
Diseñar e implementar **BookHub**, una plataforma SaaS (Software as a Service) para la gestión bibliotecaria, utilizando una arquitectura de **Microservicios** orquestada por contenedores. El objetivo es desacoplar la lógica de negocio en dominios acotados (Usuarios, Inventario, Préstamos) garantizando la independencia de despliegue y escalabilidad horizontal.

# 3. Objetivos Específicos (Técnicos)
1.  **Desacoplamiento de Datos**: Implementar el patrón *Database per Service*, segregando esquemas SQL para asegurar que ningún servicio pueda violar la integridad de otro mediante accesos directos a la BD.
2.  **Seguridad Centralizada**: Implementar un **API Gateway** como punto de entrada único que gestione *Cross-Cutting Concerns* como CORS, enrutamiento y rate-limiting básico.
3.  **Comunicación Inter-Servicios**: Establecer canales de comunicación sincrona utilizando **WebClient (Reactivo)** para la validación de integridad referencial distribuida (ej: Validar existencia de Usuario antes de crear Préstamo).
4.  **Infraestructura como Código (IaC)**: Definir la totalidad de la infraestructura mediante `Dockerfile` y `docker-compose.yml`, eliminando dependencias del entorno local del desarrollador.

# 4. Justificación Tecnológica
La elección de **Spring Boot** se justifica por su robustez, ecosistema maduro (Spring Cloud, Spring Data) y facilidad para crear aplicaciones *Cloud-Native*.
**Docker** se selecciona para garantizar el aislamiento de procesos y dependencias, permitiendo que el Frontend (Node/Nginx) y los Backends (Java) convivan sin conflictos de librerías.
**Angular** provee una SPA (Single Page Application) reactiva que consume la API REST, mejorando la experiencia de usuario (UX) al evitar recargas completas de página.

# 5. Arquitectura del Sistema

## 5.1 Patrón de Microservicios
El sistema implementa una arquitectura distribuida con los siguientes componentes:
- **API Gateway (`bookhub-gateway`)**: Punto de entrada único (Puerto 8080) implementado con Spring Cloud Gateway. Enruta peticiones y maneja seguridad básica (Filtros).
- **Service Users (`service-usuarios`)**: Microservicio (Puerto 8081) encargado de la autenticación (JWT) y gestión de usuarios.
- **Service Inventory (`service-inventario`)**: Microservicio (Puerto 8082) encargado de libros, autores, categorías y ejemplares.
- **Service Loans (`service-prestamos`)**: Microservicio (Puerto 8083) encargado de la lógica de negocio de préstamos y devoluciones.

## 5.2 Estructura de Base de Datos
Se utiliza MySQL contenedorizado (`bookhub-mysql`), con esquemas lógicos separados para garantizar desacoplamiento:
1.  **`bookhub_usuarios`**: Tabla `usuarios`.
2.  **`bookhub_inventario`**: Tablas `libro`, `autor`, `categoria`, `ejemplar` (con Relaciones FK).
3.  **`bookhub_prestamos`**: Tabla `prestamo`.

# 6. API REST y Servicios

## Microservicio Usuarios
- **Entidad**: `Usuario` (id, email, password, activo, rol).
- **Endpoints**:
    - `POST /usuarios/login`: Autenticación.
    - `POST /usuarios/create`: Registro público.

## Microservicio Inventario
- **Entidades**: `Libro`, `Ejemplar`.
- **Endpoints**:
    - `GET /inventario/libros`: Catálogo público.
    - `POST /inventario/ejemplares`: Gestión de inventario físico.

## Microservicio Préstamos
- **Entidad**: `Prestamo`.
- **Endpoints**:
    - `POST /prestamos`: Crear nuevo préstamo (Valida disponibilidad vía WebClient).
    - `PUT /prestamos/{id}/devolver`: Registrar devolución.

# 7. Docker y Despliegue

El proyecto incluye un archivo `docker-compose.yml` que orquesta 6 contenedores:
1.  MySQL (Persistencia).
2.  Service Usuarios (Backend).
3.  Service Inventario (Backend).
4.  Service Prestamos (Backend).
5.  API Gateway (Router).
6.  Angular Frontend (Nginx).

**Salud del Sistema (Healthchecks):**
Se implementaron healthchecks en los servicios críticos (MySQL, Usuarios) para evitar condiciones de carrera (Race Conditions) al iniciar el sistema.

# 8. Capturas del Sistema

> **NOTA PARA EL ESTUDIANTE:** Inserte aquí las capturas de pantalla de su sistema funcionando.

## 8.1 Login y Registro
*[Inserte Captura del Login]*

## 8.2 Dashboard del Lector (Catálogo)
*[Inserte Captura del Catálogo]*

## 8.3 Gestión de Préstamos (Admin)
*[Inserte Captura de la creación de un préstamo]*

# 9. Conclusiones y Lecciones Aprendidas

## 9.1 Desafíos Técnicos Superados
- **Orquestación de Inicio (Race Conditions)**: Se detectó que el API Gateway iniciaba antes que los microservicios downstream, provocando errores 503. Se solucionó implementando **Healthchecks** en Docker Compose, forzando al Gateway a esperar el estado `healthy` de los servicios.
- **Transaccionalidad Distribuida**: Al no usar un orquestador de transacciones (Saga), se asumió consistencia eventual. Se aprendió la importancia de diseñar operaciones idempotentes.

## 9.2 Valor de la Arquitectura
La arquitectura de microservicios, aunque introduce complejidad operativa, demostró su valor al permitir desarrollar y desplegar el módulo de `service-prestamos` sin afectar la disponibilidad del módulo de `service-usuarios`.

---
**Repositorio GitHub:** https://github.com/Jaider-Dev/bookhub
