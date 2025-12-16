import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth-guard';
import { AdminGuard } from './guards/admin-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Lazy-load standalone components to improve initial load
  { path: 'login', loadComponent: () => import('./components/login/login').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./components/register/register').then(m => m.RegisterComponent) },
  { path: 'password-recovery', loadComponent: () => import('./components/password-recovery/password-recovery').then(m => m.PasswordRecoveryComponent) },

  {
    path: 'reader',
    loadComponent: () => import('./components/dashboard-reader/dashboard-reader').then(m => m.DashboardReaderComponent),
    canActivate: [AuthGuard]
  },

  {
    path: 'admin',
    loadComponent: () => import('./components/dashboard-admin/dashboard-admin').then(m => m.DashboardAdminComponent),
    canActivate: [AdminGuard]
  },

  { path: '**', redirectTo: '/reader' }
];