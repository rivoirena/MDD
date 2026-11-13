// core/services/comment.service.ts
import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { Comment } from '../models/comment';
import { environment } from '../../../environments/environment';

@Service()
export class CommentApi {
  private http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}/articles`;

  getCommentsByArticleId(articleId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.baseUrl}/${articleId}/comments`);
  }

  createComment(articleId: number, content: string): Observable<Comment> {
    return this.http.post<Comment>(`${this.baseUrl}/${articleId}/comments`, { content });
  }
}