import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../core/services/auth';
import { RegisterRequest } from '../../core/models/registerRequest.interface';
import { JwtResponse } from '../../core/models/jwtResponse.interface';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  private fb = inject(FormBuilder);
  private auth = inject(Auth);
  private router = inject(Router);

  public onError = false;
  
  public form = this.fb.group({
    username: ['', [Validators.required, Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(255)]],
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(40),
        Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/),
      ],
    ],
  });

  public submit(): void {
    if (this.form.invalid) return;

    const registerRequest = this.form.getRawValue() as RegisterRequest;

    this.auth.register(registerRequest).subscribe({
      next: (session: JwtResponse) => {
        localStorage.setItem('token', session.token);
        this.router.navigate(['/articles']);
      },
      error: () => (this.onError = true),
    });
  }
}