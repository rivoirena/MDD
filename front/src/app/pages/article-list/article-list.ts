import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { ArticleCard } from '../../components/article-card/article-card';
import { ArticleService } from '../../core/services/article.service';

@Component({
  selector: 'app-article-list',
  imports: [ArticleCard],
  templateUrl: './article-list.html',
  styleUrl: './article-list.scss',
})
export class ArticleList {
  private readonly articleService = inject(ArticleService);

  articles = toSignal(this.articleService.getAll(), { initialValue: [] });
}