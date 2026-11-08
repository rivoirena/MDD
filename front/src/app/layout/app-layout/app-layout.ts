import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { AppHeader } from '../app-header/app-header';
import { Auth } from '../../core/services/auth';

@Component({
  imports: [
    RouterLink,
    RouterOutlet,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    AppHeader,
  ],
  selector: 'app-app-layout',
  styleUrl: './app-layout.scss',
  templateUrl: './app-layout.html',
})
export class AppLayout {
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/');
  }
}