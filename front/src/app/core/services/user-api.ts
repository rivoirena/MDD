import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { User } from '../models/user';
import { UpdateProfileRequest } from '../models/update-profile-request';
import { JwtResponse } from '../models/jwtResponse.interface';
import { Theme } from '../models/theme';

@Injectable({ providedIn: 'root' })
export class UserApi {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/users';

  currentUser = signal<User | null>(null);

  loadProfile(): Observable<User> {
    return this.http
      .get<User>(`${this.baseUrl}/me`)
      .pipe(tap((user) => this.currentUser.set(user)));
  }

  updateProfile(request: UpdateProfileRequest): Observable<JwtResponse> {
    return this.http.put<JwtResponse>(`${this.baseUrl}/me`, request).pipe(
      tap((res) => localStorage.setItem('token', res.token)), // adaptez à votre AuthService
    );
  }
  
  getSubscriptions(): Observable<Theme[]> {
    return this.http.get<Theme[]>(`${this.baseUrl}/me/subscriptions`);
  }
}
