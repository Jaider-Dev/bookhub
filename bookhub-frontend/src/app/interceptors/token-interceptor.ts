import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Auth } from '../services/auth';

// Definición del interceptor como función
export const TokenInterceptor: HttpInterceptorFn = (req, next) => {
  
  // Usamos 'inject' para obtener el servicio Auth en el contexto funcional
  const authService = inject(Auth);
  const token = authService.getToken();

  if (token) {
    // Clona la solicitud original y añade el encabezado Authorization
    const clonedRequest = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(clonedRequest);
  }

  // Si no hay token, continúa con la solicitud original
  return next(req);
};