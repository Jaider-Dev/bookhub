# Documentación API REST

El sistema expone una API RESTful centralizada a través del puerto **8080** (Gateway).

## 1. Flujos de Comunicación (Diagramas de Secuencia)

### 1.1 Flujo de Autenticación (Login)
El usuario envía sus credenciales, y el sistema valida contra la base de datos segura, retornando un Token JWT.

```mermaid
sequenceDiagram
    participant C as Cliente (Frontend)
    participant G as API Gateway
    participant U as Service Usuarios
    participant DB as MySQL (Usuarios)

    C->>G: POST /usuarios/login {email, pass}
    G->>U: Enruta petición
    U->>DB: SELECT * FROM usuarios WHERE email=?
    DB-->>U: Retorna Usuario (Hashed Pass)
    U->>U: Valida Password (BCrypt)
    alt Password Valida
        U->>U: Genera JWT
        U-->>G: 200 OK {token}
        G-->>C: 200 OK {token}
    else Password Invalida
        U-->>G: 401 Unauthorized
        G-->>C: 401 Unauthorized
    end
```

### 1.2 Flujo de Creación de Préstamo
Este flujo es crítico ya que involucra la orquestación de 3 microservicios.

```mermaid
sequenceDiagram
    participant C as Cliente
    participant P as Service Prestamos
    participant I as Service Inventario
    participant U as Service Usuarios

    C->>P: POST /prestamos {usuarioId, ejemplarId}
    
    par Validación Paralela/Secuencial
        P->>U: GET /usuarios/{id}
        U-->>P: 200 OK (Usuario Existe y Activo)
        
        P->>I: GET /inventario/ejemplares/{id}
        I-->>P: 200 OK (Ejemplar Existe y DISPONIBLE)
    end
    
    alt Validaciones Exitosas
        P->>P: Guarda Prestamo (Estado: ACTIVO)
        P->>I: PUT /ejemplares/{id}/estado (PRESTADO)
        P-->>C: 201 Created (Prestamo JSON)
    else Fallo en Validación
        P-->>C: 400 Bad Request (Error Msg)
    end
```

## 2. Catálogo de Endpoints

### 2.1 Módulo Usuarios
**Base URL:** `http://localhost:8080/usuarios`

| Método | Endpoint | Descripción | Body Requerido |
| :--- | :--- | :--- | :--- |
| `POST` | `/login` | Inicia sesión | `{"email": "...", "password": "..."}` |
| `POST` | `/` | Registra usuario (Admin) | `{"nombre": "...", "email": "...", ...}` |
| `GET` | `/` | Lista usuarios | N/A |

### 2.2 Módulo Inventario
**Base URL:** `http://localhost:8080/inventario`

| Método | Endpoint | Descripción | Body Requerido |
| :--- | :--- | :--- | :--- |
| `GET` | `/libros` | Lista el catálogo | N/A |
| `POST` | `/libros` | Crea nuevo libro | `{"titulo": "...", "autor": "...", ...}` |
| `PUT` | `/ejemplares/{id}` | Actualiza estado | `{"estado": "PRESTADO"}` |

### 2.3 Módulo Préstamos
**Base URL:** `http://localhost:8080/prestamos`

| Método | Endpoint | Descripción | Body Requerido |
| :--- | :--- | :--- | :--- |
| `POST` | `/` | Registra préstamo | `{"usuarioId": 1, "ejemplarId": 5}` |
| `PUT` | `/{id}/devolver` | Finaliza préstamo | N/A |
| `GET` | `/` | Historial préstamos | N/A |

---

## Imagenes del consumo de APIs

**POST** - http://localhost:8080/usuarios/login
![POST - http://localhost:8080/usuarios/login](img/image.png)

**GET** - http://localhost:8080/usuarios
![GET - http://localhost:8080/usuarios](img/image-1.png)