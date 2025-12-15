# 🔐 Guía de Prueba - Sistema de Login

## ✅ Servicios Corriendo

Todos los servicios deben estar en ejecución:
- **Frontend Angular**: http://localhost:4200
- **Gateway**: http://localhost:8080
- **service-usuarios**: http://localhost:8081
- **service-inventario**: http://localhost:8082
- **service-prestamos**: http://localhost:8083

## 📝 Credenciales de Prueba

El sistema crea automáticamente un usuario de prueba cuando se inicia `service-usuarios`:

```
📧 Email: admin@bookhub.co
🔑 Contraseña: adminpassword
👤 Rol: ADMIN
```

## 🧪 Cómo Probar el Login

### 1. Abre el Navegador
```
http://localhost:4200
```

### 2. Verás la Página de Login
- Se muestra un formulario con Email y Contraseña
- **ABAJO** del formulario aparece un recuadro azul con las credenciales de prueba
- El botón dice "Iniciar Sesión"

### 3. Copia las Credenciales
- Email: `admin@bookhub.co`
- Contraseña: `adminpassword`

### 4. Haz Clic en "Iniciar Sesión"
- El botón cambiará a "Cargando..." mientras se procesa
- Los campos de entrada se deshabilitarán
- El navegador hace una petición POST a: `http://localhost:8080/usuarios/login`

### 5. ¿Qué Debería Pasar?

#### ✅ Si el Login es EXITOSO:
- El botón vuelve a la normalidad
- **Se redirige automáticamente a `/admin`** (porque el usuario es ADMIN)
- En la consola del navegador (F12 → Console) verás:
  ```
  ✅ Login exitoso
  🔐 Redirigiendo a /admin (Usuario es ADMIN)
  ```
- Se guarda un **JWT token** en `localStorage` con clave `jwt_token`

#### ❌ Si el Login FALLA:
- Aparece un mensaje de error en **rojo** debajo del título
- Dice: "Credenciales inválidas o acceso denegado."
- En la consola verás:
  ```
  ❌ Error de autenticación: {error details}
  ```

## 🔍 Cómo Verificar que Está Funcionando

### Opción 1: Ver los Logs en la Consola (F12)
```javascript
// Abre el navegador y presiona F12
// Ve a la pestaña "Console"
// Deberías ver:
✅ Login exitoso
🔐 Redirigiendo a /admin (Usuario es ADMIN)
```

### Opción 2: Verificar el Token JWT
```javascript
// En la consola del navegador, ejecuta:
localStorage.getItem('jwt_token')

// Deberería devolver algo como:
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBib29raHViLmNvIiwicm9sZSI6IkFETUluIiwiaWF0IjoxNzM1MjI..."
```

### Opción 3: Decodificar el Token
```javascript
// En la consola:
const token = localStorage.getItem('jwt_token');
const decoded = JSON.parse(atob(token.split('.')[1]));
console.log(decoded);

// Verás:
{
  "sub": "admin@bookhub.co",
  "role": "ADMIN",
  "iat": 1735227...
}
```

### Opción 4: Ver la Página Admin
Si el login fue exitoso, deberías ver:
- La navbar con opciones de navegación
- Un mensaje o dashboard indicando que eres admin
- En la URL: `http://localhost:4200/admin`

## 📊 Flujo Completo del Login

```
┌─────────────────────────────────────────┐
│         Usuario en Navegador             │
│     http://localhost:4200/login         │
└──────────────┬──────────────────────────┘
               │ Usuario ingresa:
               │ - Email: admin@bookhub.co
               │ - Password: adminpassword
               ↓
┌──────────────────────────────────────────┐
│    Angular LoginComponent.onLogin()       │
│   Llama a: AuthService.login()            │
└──────────────┬───────────────────────────┘
               │ POST HTTP
               ↓
┌──────────────────────────────────────────┐
│   Gateway (http://localhost:8080)        │
│   Ruta: POST /usuarios/login             │
│   → Redirige a service-usuarios          │
└──────────────┬───────────────────────────┘
               │
               ↓
┌──────────────────────────────────────────┐
│  service-usuarios (puerto 8081)          │
│  AuthController.login()                  │
│  - Busca usuario por email               │
│  - Verifica contraseña (bcrypt)          │
│  - Genera JWT token                      │
│  - Retorna: { "token": "eyJ..." }        │
└──────────────┬───────────────────────────┘
               │ Respuesta JSON
               ↓
┌──────────────────────────────────────────┐
│   Angular Auth Service                   │
│   - Guarda token en localStorage         │
│   - Decodifica JWT                       │
│   - Verifica role: "ADMIN"               │
└──────────────┬───────────────────────────┘
               │
               ↓
┌──────────────────────────────────────────┐
│   Angular Router.navigate(['/admin'])    │
│   Redirige a Dashboard Admin             │
│   El AuthGuard valida que hay token      │
│   El AdminGuard valida que role=ADMIN    │
└──────────────────────────────────────────┘
```

## 🐛 Solución de Problemas

### "Credenciales inválidas"
- Verifica que escribiste exactamente:
  - Email: `admin@bookhub.co` (no `admin@bookhub` ni otro)
  - Contraseña: `adminpassword` (sin espacios)
- Verifica que service-usuarios está corriendo en puerto 8081
- Revisa los logs de service-usuarios

### "No redirecciona a /admin"
- Abre F12 → Console y busca errores
- Verifica que el token se guardó: `localStorage.getItem('jwt_token')`
- Revisa que la ruta `/admin` existe en `app.routes.ts`

### "CORS error"
- Verifica que el Gateway está corriendo
- Verifica que el servicio-usuarios tiene CORS habilitado
- Los errores CORS aparecen en rojo en la consola (F12)

### "No se ve la página de admin"
- Verifica que `DashboardAdminComponent` existe
- Revisa que el `AuthGuard` no bloquea el acceso
- Verifica que el token tiene `role: "ADMIN"`

## 📱 Pruebas Adicionales

### Probar con un usuario diferente (si existe)
Si creaste otros usuarios, puedes probarlos de la misma forma.

### Crear un nuevo usuario de prueba
En MySQL (si lo necesitas):
```sql
INSERT INTO bookhub_usuarios.usuarios (nombre, email, password, cedula, rol, activo, telefono)
VALUES ('Test User', 'test@bookhub.co', 'hashed_password', '1234567890', 'USER', true, '');
```

Nota: La contraseña debe estar hasheada con bcrypt.

## ✨ Indicadores de Éxito

✅ Entrada sin errores
✅ Redirección a `/admin` o `/reader` según el rol
✅ Token guardado en localStorage
✅ Navbar visible con opciones de navegación
✅ Ningún error CORS en la consola
✅ Logs muestran la validación de credenciales

¡Si ves todo esto, ¡tu sistema de login está funcionando perfectamente! 🎉
