import { Component, inject, output } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-app-header',
  imports: [
    RouterLink,
    RouterLinkActive,
    MatButtonModule,
    MatIconModule,
    MatToolbarModule,
  ],
  templateUrl: './app-header.html',
  styleUrl: './app-header.scss',
})
export class AppHeader {
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);

  readonly menuToggle = output<void>();

  logout(): void {
    this.auth.logout();
    this.router.navigateByUrl('/');
  }
}