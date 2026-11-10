import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ArticleCard } from './article-card';
import { Article } from '../../core/models/article';

describe('ArticleCard', () => {
  let fixture: ComponentFixture<ArticleCard>;

  const article: Article = {
    id: 42,
    title: 'Mon article',
    content: 'Contenu de test',
    createdAt: '2024-06-15T12:00:00Z',
    authorId: 7,
    authorName: 'Camille',
    themeId: 3,
    themeName: 'Angular',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArticleCard],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleCard);
    fixture.componentRef.setInput('article', article);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display the article details', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('mat-card-title')?.textContent).toContain('Mon article');
    expect(element.querySelector('p')?.textContent).toContain('Contenu de test');
    expect(element.textContent).toContain('Camille');
    expect(element.textContent).toContain('15/06/2024');
  });

  it('should link to the article detail page', () => {
    const link = (fixture.nativeElement as HTMLElement).querySelector('a');

    expect(link?.getAttribute('href')).toBe('/articles/42');
  });
});