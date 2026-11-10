import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { ArticleApi } from '../../core/services/article-api';
import { CommentApi } from '../../core/services/comment-api';
import { ArticleDetail } from './article-detail';

describe('ArticleDetail', () => {
  let fixture: ComponentFixture<ArticleDetail>;
  let component: ArticleDetail;

  let articleApi: { getArticleById: jest.Mock };
  let commentApi: {
    getCommentsByArticleId: jest.Mock;
    createComment: jest.Mock;
  };
  let router: { navigate: jest.Mock };

  const article = {
    id: 12,
    title: 'Mon article',
    content: 'Contenu de l’article',
    createdAt: '2026-01-15',
    authorName: 'Jean',
    themeName: 'Angular',
  };

  const comments = [
    { id: 1, authorName: 'Marie', content: 'Très intéressant !' },
  ];

  beforeEach(async () => {
    articleApi = {
      getArticleById: jest.fn().mockReturnValue(of(article)),
    };

    commentApi = {
      getCommentsByArticleId: jest.fn().mockReturnValue(of(comments)),
      createComment: jest.fn(),
    };

    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [ArticleDetail],
      providers: [
        { provide: ArticleApi, useValue: articleApi },
        { provide: CommentApi, useValue: commentApi },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleDetail);
    component = fixture.componentInstance;

    fixture.componentRef.setInput('id', '12');
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should create and load the article and comments', () => {
    expect(component).toBeTruthy();
    expect(articleApi.getArticleById).toHaveBeenCalledWith(12);
    expect(commentApi.getCommentsByArticleId).toHaveBeenCalledWith(12);
  });

  it('should display the article and its comments', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent;

    expect(text).toContain('Mon article');
    expect(text).toContain('Contenu de l’article');
    expect(text).toContain('Très intéressant !');
  });

  it('should navigate back to the articles list', () => {
    component.back();

    expect(router.navigate).toHaveBeenCalledWith(['/articles']);
  });

  it('should not create a comment when the form is empty', async () => {
    component.onSubmit(new Event('submit'));
    await fixture.whenStable();

    expect(commentApi.createComment).not.toHaveBeenCalled();
  });

  it('should create a comment and reload the comments', async () => {
    commentApi.createComment.mockReturnValue(of({
      id: 2,
      authorName: 'Jean',
      content: 'Mon commentaire',
    }));

    const reloadSpy = jest.spyOn(component.comments, 'reload');
    component.model.set({ content: 'Mon commentaire' });

    component.onSubmit(new Event('submit'));
    await fixture.whenStable();

    expect(commentApi.createComment).toHaveBeenCalledWith(
      12,
      'Mon commentaire',
    );
    expect(reloadSpy).toHaveBeenCalled();
    expect(component.model().content).toBe('');
  });
});