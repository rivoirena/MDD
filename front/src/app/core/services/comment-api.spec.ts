import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { CommentApi } from './comment-api';
import { Comment } from '../models/comment';
import { environment } from '../../../environments/environment';

describe('CommentApi', () => {
  let service: CommentApi;
  let httpTestingController: HttpTestingController;

  const baseUrl = `${environment.apiUrl}/articles`;

  const comment: Comment = {
    id: 1,
    content: 'Super article !',
    authorId: 2,
    authorName: 'Alice',
    articleId: 42,
    createdAt: '2026-01-15T10:30:00',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CommentApi, provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(CommentApi);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getCommentsByArticleId', () => {
    it('should fetch comments for the requested article', () => {
      const articleId = 42;
      const comments: Comment[] = [comment];

      service.getCommentsByArticleId(articleId).subscribe((result) => {
        expect(result).toEqual(comments);
      });

      const request = httpTestingController.expectOne(`${baseUrl}/${articleId}/comments`);

      expect(request.request.method).toBe('GET');
      request.flush(comments);
    });
  });

  describe('createComment', () => {
    it('should post the comment content for the requested article', () => {
      const articleId = 42;
      const content = 'Super article !';

      service.createComment(articleId, content).subscribe((result) => {
        expect(result).toEqual(comment);
      });

      const request = httpTestingController.expectOne(`${baseUrl}/${articleId}/comments`);

      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual({ content });
      request.flush(comment);
    });
  });
});
