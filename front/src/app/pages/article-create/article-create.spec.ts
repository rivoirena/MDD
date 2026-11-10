import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ArticleApi } from '../../core/services/article-api';
import { ThemeApi } from '../../core/services/theme-api';
import { ArticleCreate } from './article-create';

describe('ArticleCreate', () => {
  let fixture: ComponentFixture<ArticleCreate>;
  let component: ArticleCreate;

  let articleApi: { create: jest.Mock };
  let themeApi: { getAll: jest.Mock };
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    articleApi = {
      create: jest.fn(),
    };

    themeApi = {
      getAll: jest.fn().mockReturnValue(
        of([{ id: 1, title: 'Angular', description: 'Thème Angular' }]),
      ),
    };

    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [ArticleCreate],
      providers: [
        { provide: ArticleApi, useValue: articleApi },
        { provide: ThemeApi, useValue: themeApi },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ArticleCreate);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should create and load themes', () => {
    expect(component).toBeTruthy();
    expect(themeApi.getAll).toHaveBeenCalled();
  });

  it('should not create an article when the form is invalid', () => {
    component.create(new Event('submit'));

    expect(articleApi.create).not.toHaveBeenCalled();
  });

  it('should create an article and navigate to articles', () => {
    articleApi.create.mockReturnValue(of({}));
    component.model.set({
      themeId: 1,
      title: 'Mon article',
      content: 'Contenu de mon article',
    });

    component.create(new Event('submit'));

    expect(articleApi.create).toHaveBeenCalledWith({
      themeId: 1,
      title: 'Mon article',
      content: 'Contenu de mon article',
    });
    expect(router.navigate).toHaveBeenCalledWith(['/articles']);
  });

  it('should display the API error message when creation fails', () => {
    articleApi.create.mockReturnValue(
      throwError(() => ({
        error: { message: 'Erreur personnalisée' },
      })),
    );
    component.model.set({
      themeId: 1,
      title: 'Mon article',
      content: 'Contenu de mon article',
    });

    component.create(new Event('submit'));

    expect(component.errorMessage()).toBe('Erreur personnalisée');
  });

  it('should display a fallback message when the API error has no message', () => {
    articleApi.create.mockReturnValue(
      throwError(() => ({ error: {} })),
    );
    component.model.set({
      themeId: 1,
      title: 'Mon article',
      content: 'Contenu de mon article',
    });

    component.create(new Event('submit'));

    expect(component.errorMessage()).toBe(
      "Erreur lors de la création de l'article",
    );
  });

  it('should navigate back to articles', () => {
    component.back();

    expect(router.navigate).toHaveBeenCalledWith(['/articles']);
  });
});