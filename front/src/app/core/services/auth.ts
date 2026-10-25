import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { RegisterRequest } from '../models/registerRequest.interface';
import { JwtResponse } from '../models/jwtResponse.interface';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class Auth {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  public register(registerRequest: RegisterRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(
      `${this.baseUrl}/register`,
      registerRequest
    );
  }
}