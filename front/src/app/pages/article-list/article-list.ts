import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';

import { ArticleCard } from '../../components/article-card/article-card';
import { ArticleApi } from '../../core/services/article-api';

@Component({
  selector: 'app-article-list',
  imports: [ArticleCard],
  templateUrl: './article-list.html',
  styleUrl: './article-list.scss',
})
export class ArticleList {
  private readonly articleApi = inject(ArticleApi);

  articles = rxResource({
    stream: () => this.articleApi.getAll(),
    defaultValue: [],
  });
}
