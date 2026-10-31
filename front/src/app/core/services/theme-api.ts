import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { Theme } from '../models/theme';
import { environment } from '../../../environments/environment';

@Service()
export class ThemeApi {
  private http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/themes`;

  getAll(): Observable<Theme[]> {
    return this.http.get<Theme[]>(this.baseUrl);
  }
}
