import { Component, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';

import { ArticleCard } from '../../components/article-card/article-card';
import { ArticleApi } from '../../core/services/article-api';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';

type SortOrder = 'desc' | 'asc';

@Component({
  selector: 'app-article-list',
  imports: [RouterLink, MatButtonModule, MatIconModule, ArticleCard, MatMenuModule],
  templateUrl: './article-list.html',
  styleUrl: './article-list.scss',
})
export class ArticleList {
  private readonly articleApi = inject(ArticleApi);

  sortOrder = signal<SortOrder>('desc');

  articles = rxResource({
    params: () => this.sortOrder(),
    stream: ({ params }) => this.articleApi.getAll(params),
    defaultValue: [],
  });
}
