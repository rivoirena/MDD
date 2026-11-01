import { Component, inject, signal } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginRequest } from '../../core/models/login-request';
import { Auth } from '../../core/services/auth';
import { form, FormField, required } from '@angular/forms/signals';

@Component({
  selector: 'app-login',
  imports: [FormField],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private fb = inject(FormBuilder);
  private auth = inject(Auth);
  private router = inject(Router);

  onError = signal(false);

  model = signal<LoginRequest>({ identifier: '', password: '' });

  loginForm = form(this.model, (p) => {
    required(p.identifier, { message: 'Identifiant requis' });
    required(p.password, { message: 'Mot de passe requis' });
  });

  submit(event: Event): void {
    event.preventDefault();
    if (this.loginForm().invalid()) return;

    this.onError.set(false);

    this.auth.login(this.model()).subscribe({
      next: () => this.router.navigate(['/articles']),
      error: () => this.onError.set(true),
    });
  }
}