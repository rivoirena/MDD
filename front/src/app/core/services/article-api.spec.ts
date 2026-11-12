import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ArticleApi } from './article-api';
import { Article } from '../models/article';
import { CreateArticleRequest } from '../models/create-article-request';
import { environment } from '../../../environments/environment';

describe('ArticleApi', () => {
  let service: ArticleApi;
  let httpTestingController: HttpTestingController;

  const baseUrl = `${environment.apiUrl}/articles`;

  const article: Article = {
    id: 42,
    title: 'Mon article',
    content: 'Mon contenu',
    createdAt: '2026-11-10T20:47:00',
    authorId: 1,
    authorName: 'Alice',
    themeId: 1,
    themeName: 'Développement',
  };

  const payload: CreateArticleRequest = {
    themeId: 1,
    title: 'Mon article',
    content: 'Mon contenu',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ArticleApi,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(ArticleApi);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getAll', () => {
    it('should request articles sorted by descending order by default', () => {
      const articles: Article[] = [article];

      service.getAll().subscribe((result) => {
        expect(result).toEqual(articles);
      });

      const request = httpTestingController.expectOne(
        (req) => req.url === baseUrl && req.params.get('sort') === 'desc',
      );

      expect(request.request.method).toBe('GET');
      request.flush(articles);
    });

    it('should request articles with the requested sort order', () => {
      const articles: Article[] = [article];

      service.getAll('asc').subscribe((result) => {
        expect(result).toEqual(articles);
      });

      const request = httpTestingController.expectOne(
        (req) => req.url === baseUrl && req.params.get('sort') === 'asc',
      );

      expect(request.request.method).toBe('GET');
      request.flush(articles);
    });
  });

  describe('getArticleById', () => {
    it('should request the article by id and return it', () => {
      service.getArticleById(article.id).subscribe((result) => {
        expect(result).toEqual(article);
      });

      const request = httpTestingController.expectOne(
        `${baseUrl}/${article.id}`,
      );

      expect(request.request.method).toBe('GET');
      request.flush(article);
    });
  });

  describe('create', () => {
    it('should send the request and return the created article', () => {
      service.create(payload).subscribe((result) => {
        expect(result).toEqual(article);
      });

      const request = httpTestingController.expectOne(baseUrl);

      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual(payload);
      request.flush(article);
    });
  });
});