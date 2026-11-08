import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Article } from '../models/article';

import { CreateArticleRequest } from '../models/create-article-request';

@Injectable({ providedIn: 'root' })
export class ArticleApi {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/articles`;

  getAll(sort: 'asc' | 'desc' = 'desc') {
    return this.http.get<Article[]>(`${this.baseUrl}`, { params: { sort } });
  }

  getArticleById(id: number): Observable<Article> {
    return this.http.get<Article>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateArticleRequest): Observable<Article> {
    return this.http.post<Article>(this.baseUrl, request);
  }
}
