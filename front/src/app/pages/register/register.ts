import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Auth } from '../../core/services/auth';
import { RegisterRequest } from '../../core/models/register-request';
import {
  email,
  form,
  FormField,
  maxLength,
  minLength,
  pattern,
  required,
} from '@angular/forms/signals';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-register',
  imports: [
    RouterLink,
    MatButtonModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    FormField,
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  private fb = inject(FormBuilder);
  private auth = inject(Auth);
  private router = inject(Router);

  onError = signal(false);

  model = signal<RegisterRequest>({ username: '', email: '', password: '' });

  registerForm = form(this.model, (p) => {
    required(p.username, { message: "Nom d'utilisateur requis" });
    maxLength(p.username, 50, { message: '50 caractères maximum' });

    required(p.email, { message: 'Email requis' });
    email(p.email, { message: 'Email invalide' });
    maxLength(p.email, 255, { message: '255 caractères maximum' });

    required(p.password, { message: 'Mot de passe requis' });
    minLength(p.password, 8, { message: '8 caractères minimum' });
    maxLength(p.password, 40, { message: '40 caractères maximum' });
    pattern(p.password, /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/, {
      message: 'Une minuscule, une majuscule, un chiffre et un caractère spécial',
    });
  });

  submit(event: Event): void {
    event.preventDefault();
    if (this.registerForm().invalid()) return;

    this.onError.set(false);

    this.auth.register(this.model()).subscribe({
      next: () => this.router.navigate(['/articles']),
      error: () => this.onError.set(true),
    });
  }
}
