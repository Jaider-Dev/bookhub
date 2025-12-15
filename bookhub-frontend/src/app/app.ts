import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router'; // Módulo para <router-outlet>

@Component({
  selector: 'app-root',
  standalone: true, // ¡CLAVE!
  imports: [RouterOutlet], // Router para navegación
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent {
  title = 'bookhub-frontend';
}