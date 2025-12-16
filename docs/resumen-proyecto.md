# 1. Descripción del problema

En la actualidad, muchas bibliotecas pequeñas o comunitarias gestionan su inventario y préstamos de manera manual o mediante hojas de cálculo desconectadas. Esto conlleva problemas como:
- Desactualización del inventario en tiempo real.
- Errores humanos en el registro de préstamos y devoluciones.
- Dificultad para mantener una base de datos de usuarios segura y centralizada.
- Falta de accesibilidad para que los usuarios consulten la disponibilidad de libros desde internet.

La ausencia de un sistema integrado dificulta el control de los recursos y empeora la experiencia tanto de los bibliotecarios como de los lectores.

# 2. Objetivo general de la aplicación

Desarrollar una aplicación web basada en una arquitectura de microservicios que permita gestionar de manera integral el inventario de libros, el registro de usuarios y el control de préstamos de la biblioteca **Bookhub**, garantizando la persistencia de datos y facilitando su despliegue mediante contenedores Docker.

# 3. Objetivos específicos

- **Diseñar y estructurar la base de datos** relacional (MySQL) para persistir la información de usuarios, libros, ejemplares, autores, categorías y préstamos, asegurando la integridad referencial.
- **Implementar una API REST** robusta utilizando Spring Boot (Java) para exponer los servicios de negocio y permitir la comunicación entre el frontend y el backend.
- **Integrar los microservicios** mediante el patrón API Gateway para centralizar el acceso y gestionar la seguridad y el enrutamiento.
- **Contenerizar los servicios** utilizando Docker y crear un archivo `docker-compose.yml` para orquestar el despliegue de todo el sistema (Base de datos, Backend y Frontend) en un solo comando.
- **Desarrollar una interfaz web** intuitiva utilizando Angular para permitir a los usuarios y administradores interactuar con el sistema.

# 4. Justificación de la aplicación

- **¿Por qué se eligió esa temática?**: La gestión de bibliotecas es un problema clásico que permite explorar a fondo los patrones de diseño, relaciones entre entidades (préstamos, inventario) y la separación de responsabilidades, siendo ideal para aplicar una arquitectura de microservicios.
- **¿Qué problema real resuelve?**: Automatiza procesos manuales, reduce el riesgo de pérdida de libros y mejora el acceso a la información cultural.
- **¿Quién lo usaría?**:
    - **Bibliotecarios (Administradores)**: Para gestionar el catálogo y registrar préstamos/devoluciones.
    - **Lectores (Usuarios)**: Para buscar libros disponibles y revisar su historial de préstamos.

# 5. Conclusiones finales del proyecto

## Dificultades
- **Configuración de Docker Networking**: Un desafío principal fue asegurar que los contenedores se comunicaran correctamente entre sí (Gateway -> Servicios) y con la base de datos, resolviendo problemas de resolución de nombres y condiciones de carrera en el inicio (`depends_on`).
- **Mapeo de Entidades**: La sincronización de modelos de datos entre el Frontend (Angular) y el Backend (Java/JPA), como el manejo de Enums y tipos de datos (Boolean vs boolean).

## Aprendizajes
- Se profundizó en el uso de **Spring Boot** y **Spring Cloud Gateway** para construir sistemas escalables.
- Se comprendió la importancia de la **Inyección de Dependencias** y los patrones de diseño.
- Se adquirió experiencia práctica en **DevOps** básico mediante la creación de Dockerfiles y la orquestación con Docker Compose.

## Recomendaciones
- Para futuras versiones, se recomienda implementar un **Service Discovery** (como Eureka) para evitar el acoplamiento a nombres de host estáticos en el Gateway.
- Añadir **Tests Unitarios** y de Integración automatizados para detectar errores de regresión antes del despliegue.
