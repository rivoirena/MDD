import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { rxResource } from '@angular/core/rxjs-interop';
import { form, FormField, required, maxLength } from '@angular/forms/signals';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { ArticleApi } from '../../core/services/article-api';
import { ThemeApi } from '../../core/services/theme-api';

@Component({
  selector: 'app-article-create',
  imports: [FormField, MatFormFieldModule, MatInputModule, MatSelectModule, MatButtonModule],
  templateUrl: './article-create.html',
  styleUrl: './article-create.scss',
})
export class ArticleCreate {
  private articleService = inject(ArticleApi);
  private themeApi = inject(ThemeApi);
  private router = inject(Router);

  themes = rxResource({
    stream: () => this.themeApi.getAll(),
  });

  model = signal({
    themeId: null as number | null,
    title: '',
    content: '',
  });

  errorMessage = signal('');

  articleForm = form(this.model, (p) => {
    required(p.themeId, { message: 'Thème requis' });

    required(p.title, { message: 'Titre requis' });
    maxLength(p.title, 255, { message: '255 caractères maximum' });

    required(p.content, { message: 'Contenu requis' });
  });

  create(event: Event): void {
    event.preventDefault();
    if (this.articleForm().invalid()) return;

    const { themeId, title, content } = this.model();

    this.errorMessage.set('');

    this.articleService.create({ themeId: themeId!, title, content }).subscribe({
      next: () => this.router.navigate(['/articles']),
      error: (err) =>
        this.errorMessage.set(err.error?.message ?? "Erreur lors de la création de l'article"),
    });
  }

  back(): void {
    this.router.navigate(['/articles']);
  }
}