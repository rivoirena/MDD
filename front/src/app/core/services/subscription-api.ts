import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { Theme } from '../models/theme';

@Service()
export class SubscriptionApi {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/users/me/subscriptions';

  getSubscriptions(): Observable<Theme[]> {
    return this.http.get<Theme[]>(this.baseUrl);
  }

subscribe(themeId: number): Observable<void> {
  return this.http.post<void>(`${this.baseUrl}/${themeId}`, null);
}

unsubscribe(themeId: number): Observable<void> {
  return this.http.delete<void>(`${this.baseUrl}/${themeId}`);
}

}
