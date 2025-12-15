import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes'; // <-- Importa las rutas
import { provideHttpClient, withInterceptors } from '@angular/common/http'; // <-- Clave para HTTP
import { TokenInterceptor } from './interceptors/token-interceptor'; // <-- Importa tu interceptor

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes), // Proporciona el sistema de rutas

    // Proporciona el cliente HTTP y registra el interceptor
    provideHttpClient(
      withInterceptors([
        // Aquí se registra tu TokenInterceptor de forma funcional
        TokenInterceptor 
      ])
    ) 
    // Nota: El servicio Auth y los Guards ya se inyectan en 'root' y no necesitan registro manual aquí.
  ]
};