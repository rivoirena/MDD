import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { RegisterRequest } from '../models/registerRequest.interface';
import { JwtResponse } from '../models/jwtResponse.interface';
import { environment } from '../../../environments/environment';
import { LoginRequest } from '../models/loginRequest.interface';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  
  private saveSession(session: JwtResponse): void {
    localStorage.setItem('token', session.token);
  }

  public register(req: RegisterRequest): Observable<JwtResponse> {
    return this.http
      .post<JwtResponse>(`${this.baseUrl}/register`, req)
      .pipe(tap((session) => this.saveSession(session)));
  }

  public login(req: LoginRequest): Observable<JwtResponse> {
    return this.http
      .post<JwtResponse>(`${this.baseUrl}/login`, req)
      .pipe(tap((session) => this.saveSession(session)));
  }
}
