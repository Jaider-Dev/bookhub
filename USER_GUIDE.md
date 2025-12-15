# 📱 BookHub - Guía de Usuario Final

## 🎯 ESTADO ACTUAL DEL PROYECTO

### ✅ LO QUE FUNCIONA AHORA

#### FRONTEND (Angular 17+)
1. **Página de Login**
   - Ingresa con correo y contraseña
   - Mensajes de error claros: "Correo o contraseña incorrecta"
   - Redirección automática según rol

2. **Dashboard ADMIN**
   - Página de inicio con estadísticas (usuarios, libros, préstamos)
   - **Gestión de Usuarios** (completa):
     - Ver todos los usuarios
     - Crear usuarios (con rol y contraseña inicial)
     - Editar usuario
     - Deshabilitar usuario (no elimina, solo desactiva)
     - Buscar por nombre, email, cédula
     - Filtrar por rol (ADMIN/LECTOR)
     - Filtrar por estado (Activo/Inactivo)
     - Ordenar por nombre, email, rol

3. **Dashboard LECTOR**
   - Página de inicio con opciones
   - **Explorar Libros**:
     - Ver catálogo completo
     - Buscar por título, autor, categoría
     - Ver disponibilidad (ejemplares)
     - Solicitar préstamo
   - **Mis Préstamos**:
     - Ver todos los préstamos del usuario
     - Estado: ACTIVO, DEVUELTO, VENCIDO
     - Fechas de préstamo y devolución
   - **Mi Perfil**:
     - Editar nombre, email, teléfono, cédula
     - (próximamente: cambiar contraseña)

#### BACKEND (Spring Boot)
- Gateway en puerto 8080
- Service-Usuarios en puerto 8081
- Service-Inventario en puerto 8082
- Service-Préstamos en puerto 8083
- Caché automático para optimizar rendimiento
- Timeouts de 8 segundos
- Reintentos automáticos

---

## ⚠️ LO QUE FALTA IMPLEMENTAR

### 🔴 CRÍTICO - Debe hacerse ahora mismo

#### 1. **Backend - Endpoints Faltantes en Service-Usuarios**
```
POST /usuarios/{id}/change-password
  - Body: { currentPassword: string, newPassword: string }
  - Función: Cambiar contraseña del usuario
```

#### 2. **Backend - Nuevos Endpoints en Service-Inventario**
```
Autores:
  GET /inventario/autores
  POST /inventario/autores
  PUT /inventario/autores/{id}
  DELETE /inventario/autores/{id}

Categorías:
  GET /inventario/categorias
  POST /inventario/categorias
  PUT /inventario/categorias/{id}
  DELETE /inventario/categorias/{id}

Ejemplares:
  GET /inventario/ejemplares
  POST /inventario/ejemplares
  PUT /inventario/ejemplares/{id}
  DELETE /inventario/ejemplares/{id}
```

#### 3. **Backend - Nuevo Endpoint en Service-Préstamos**
```
GET /prestamos/mis-prestamos
  - Retorna: List de Préstamos del usuario autenticado
  - Usa email del JWT token
```

#### 4. **Frontend - Componentes Faltantes**
- `inventario-admin.component` - Gestión de libros, autores, categorías, ejemplares
- `prestamos-admin.component` - Registrar préstamos y devoluciones
- `user-profile.component` - Mejorado con opción de cambiar contraseña

---

## 🚀 CÓMO USAR LO QUE YA EXISTE

### Login
1. Abre `http://localhost:4200/login`
2. Ingresa credenciales:
   - **ADMIN**: Email de admin, contraseña de admin
   - **LECTOR**: Email de lector, contraseña de lector

### Como ADMIN
1. Vas a `http://localhost:4200/admin`
2. **Inicio**: Ves estadísticas
3. **Gestión de Usuarios**:
   - Click en "+ Nuevo Usuario" para crear
   - Click en ✏️ para editar
   - Click en 🗑️ para deshabilitar
   - Usa filtros para buscar

### Como LECTOR
1. Vas a `http://localhost:4200/reader`
2. **Explorar Libros**:
   - Click en "Ver Catálogo"
   - Busca libros por título/autor
   - Click en "Solicitar Préstamo" para uno disponible
3. **Mis Préstamos**:
   - Click en "Ver Préstamos"
   - Ves todos tus préstamos activos

---

## 🔧 CÓMO VERIFICAR QUE FUNCIONA

### Paso 1: Verificar MySQL
```bash
# MySQL debe estar corriendo
# Base de datos: bookhub_usuarios, bookhub_inventario, bookhub_prestamos
```

### Paso 2: Iniciar Backend (4 servicios)
```bash
# Terminal 1 - Gateway
cd bookhub-gateway
mvnw spring-boot:run

# Terminal 2 - Usuarios
cd service-usuarios
mvnw spring-boot:run

# Terminal 3 - Inventario
cd service-inventario
mvnw spring-boot:run

# Terminal 4 - Préstamos
cd service-prestamos
mvnw spring-boot:run
```

### Paso 3: Iniciar Frontend
```bash
cd bookhub-frontend
npm start
# O
ng serve --port 4300
```

### Paso 4: Abrir en navegador
```
http://localhost:4200  (o 4300 si es diferente)
```

### Paso 5: Probar funcionalidades
- Login
- Ver usuarios (si eres admin)
- Ver libros (si eres lector)
- Crear préstamo (si eres lector)

---

## 📊 Requisitos del Backend Aún No Implementados

### Service-Usuarios debe tener:
```java
@PostMapping("/{id}/change-password")
public ResponseEntity<?> changePassword(
    @PathVariable Long id,
    @RequestBody Map<String, String> request
) {
    String currentPassword = request.get("currentPassword");
    String newPassword = request.get("newPassword");
    
    // 1. Verificar que la contraseña actual sea correcta
    // 2. Actualizar la nueva contraseña
    // 3. Retornar éxito o error
}
```

### Service-Inventario debe tener:
```java
// Controllers para Autor, Categoria, Ejemplar
@RestController
@RequestMapping("/inventario")
public class AutorController {
    @GetMapping("/autores")
    @PostMapping("/autores")
    @PutMapping("/autores/{id}")
    @DeleteMapping("/autores/{id}")
}

public class CategoriaController {
    @GetMapping("/categorias")
    @PostMapping("/categorias")
    @PutMapping("/categorias/{id}")
    @DeleteMapping("/categorias/{id}")
}

public class EjemplarController {
    @GetMapping("/ejemplares")
    @PostMapping("/ejemplares")
    @PutMapping("/ejemplares/{id}")
    @DeleteMapping("/ejemplares/{id}")
}
```

### Service-Préstamos debe tener:
```java
@GetMapping("/mis-prestamos")
public ResponseEntity<List<Prestamo>> getMisPrestamos(
    @AuthenticationPrincipal User user  // O extraer del JWT
) {
    // Retornar préstamos del usuario autenticado
}
```

---

## 📋 Checklist de Tareas Pendientes

### ADMIN PANEL
- [x] Dashboard con estadísticas
- [x] Gestión de Usuarios (CRUD)
- [ ] Gestión de Inventario (Libros, Autores, Categorías, Ejemplares)
- [ ] Gestión de Préstamos (Registrar, Devolver)

### LECTOR PANEL
- [x] Dashboard con opciones
- [x] Explorar y buscar libros
- [x] Solicitar préstamos
- [x] Ver mis préstamos
- [ ] Cambiar contraseña en perfil
- [ ] Descargar historial de préstamos (bonus)

### BACKEND
- [ ] Endpoint cambiar contraseña
- [ ] Controladores de Autor
- [ ] Controladores de Categoría
- [ ] Controladores de Ejemplar
- [ ] Endpoint "mis préstamos"
- [ ] Endpoint devolución de préstamo

---

## 🎨 Detalles Visuales

### Colores
- **ADMIN**: Azul (#0066cc)
- **LECTOR**: Púrpura (#667eea)
- **Éxito**: Verde (#48bb78)
- **Error**: Rojo (#f56565)
- **Inactivo**: Gris (#ddd)

### Tipografía
- Títulos: 24-32px, bold
- Subtítulos: 18px, normal
- Cuerpo: 14-16px
- Labels: 13px

### Espaciado
- Padding contenedores: 20-40px
- Margen entre elementos: 15-20px
- Gap en grillas: 15-20px

---

## 🆘 Solución de Problemas

### "No puedo hacer login"
1. Verifica que MySQL está corriendo
2. Verifica que Service-Usuarios está corriendo en puerto 8081
3. Verifica que el Gateway está en puerto 8080
4. Busca logs de error en la consola del navegador (F12)

### "No veo libros en explorar"
1. Verifica que Service-Inventario está corriendo en puerto 8082
2. Asegúrate de que hay libros en la base de datos
3. Revisa en DevTools si hay error 500 o timeout

### "No puedo crear préstamos"
1. Verifica que Service-Préstamos está corriendo en puerto 8083
2. Asegúrate de estar logueado como LECTOR
3. Verifica que el libro tiene ejemplares disponibles

### "Timeout frecuente"
1. Es normal en servidores lentos
2. Aumenta timeout de 8s a 12s en `BaseApiService`
3. Revisa recursos del servidor (RAM, CPU)

---

## 📞 Contacto & Soporte

Si necesitas ayuda:
1. Revisa los logs en consola del navegador (F12)
2. Revisa los logs de cada servicio Java
3. Verifica que todos los puertos están en uso correcto

**Puertos esperados:**
- Frontend: 4200 (o 4300)
- Gateway: 8080
- Service-Usuarios: 8081
- Service-Inventario: 8082
- Service-Préstamos: 8083
- MySQL: 3306

---

**Última actualización**: 14 Diciembre 2025
**Versión**: 1.2.0
**Status**: 🟡 En desarrollo
