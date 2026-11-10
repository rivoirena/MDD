import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { Article } from '../../core/models/article';
import { ArticleApi } from '../../core/services/article-api';
import { ArticleList } from './article-list';

describe('ArticleList', () => {
  let fixture: ComponentFixture<ArticleList>;
  let component: ArticleList;
  let articleApi: { getAll: jest.Mock };

  const articles: Article[] = [
    {
      id: 1,
      title: 'Premier article',
      content: 'Contenu du premier article',
      createdAt: '2026-01-15',
      authorId: 1,
      authorName: 'Jean',
      themeId: 1,
      themeName: 'Angular',
    },
  ];

  beforeEach(async () => {
    articleApi = {
      getAll: jest.fn().mockReturnValue(of(articles)),
    };

    await TestBed.configureTestingModule({
      imports: [ArticleList],
      providers: [
        { provide: ArticleApi, useValue: articleApi },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleList);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should create and load articles with the default sort order', () => {
    expect(component).toBeTruthy();
    expect(component.sortOrder()).toBe('desc');
    expect(articleApi.getAll).toHaveBeenCalledWith('desc');
    expect(component.articles.value()).toEqual(articles);
  });

  it('should display the article', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent;

    expect(text).toContain('Premier article');
    expect(text).toContain('Contenu du premier article');
  });

  it('should load articles in ascending order when the sort order changes', async () => {
    component.sortOrder.set('asc');
    fixture.detectChanges();
    await fixture.whenStable();

    expect(articleApi.getAll).toHaveBeenCalledWith('asc');
  });
});