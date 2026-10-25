import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { LoginRequest } from '../../core/models/loginRequest.interface';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private fb = inject(FormBuilder);
  private auth = inject(Auth);
  private router = inject(Router);

  public onError = signal(false);

  public form = this.fb.nonNullable.group({
    identifier: ['', [Validators.required]],
    password: ['', [Validators.required]],
  });

  public submit(): void {
    if (this.form.invalid) return;

    const loginRequest = this.form.getRawValue() as LoginRequest;

    this.auth.login(loginRequest).subscribe({
      next: () => this.router.navigate(['/articles']),
      error: () => this.onError.set(true),
    });
  }
}