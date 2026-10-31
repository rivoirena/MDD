import { HttpClient } from '@angular/common/http';
import { inject, Injectable, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { Theme } from '../models/theme';

@Injectable({ providedIn: 'root' })
export class SubscriptionApi {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api';

  getSubscriptions(): Observable<Theme[]> {
    return this.http.get<Theme[]>(`${this.baseUrl}/users/me/subscriptions`);
  }

}
