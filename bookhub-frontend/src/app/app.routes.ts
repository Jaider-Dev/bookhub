import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth-guard';
import { AdminGuard } from './guards/admin-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/reader', pathMatch: 'full' },

  // Lazy-load standalone components to improve initial load
  { path: 'login', loadComponent: () => import('./components/login/login').then(m => m.LoginComponent) },

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