import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { RegisterRequest } from '../models/register-request';
import { JwtResponse } from '../models/jwt-response';
import { environment } from '../../../environments/environment';
import { LoginRequest } from '../models/login-request';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'token';

  public register(registerRequest: RegisterRequest): Observable<JwtResponse> {
    return this.http
      .post<JwtResponse>(`${this.baseUrl}/register`, registerRequest)
      .pipe(tap((session) => this.saveSession(session.token)));
  }

  public login(loginRequest: LoginRequest): Observable<JwtResponse> {
    return this.http
      .post<JwtResponse>(`${this.baseUrl}/login`, loginRequest)
      .pipe(tap((session) => this.saveSession(session.token)));
  }

  public getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  public isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  public logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  private saveSession(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }
}
