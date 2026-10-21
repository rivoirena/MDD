import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Article } from '../models/article.interface';

@Injectable({ providedIn: 'root' })
export class ArticleService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/articles`;

  getAll(): Observable<Article[]> {
    return this.http.get<Article[]>(this.baseUrl);
  }
}