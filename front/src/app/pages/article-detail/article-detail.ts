import { Component, inject, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { ArticleService } from '../../core/services/article.service';

@Component({
  selector: 'app-article-detail',
  imports: [],
  templateUrl: './article-detail.html',
  styleUrl: './article-detail.scss',
})
export class ArticleDetail {
  private articleService = inject(ArticleService);

  id = input.required<string>();

  article = toSignal(
    toObservable(this.id).pipe(
      switchMap((id) => this.articleService.getArticleById(Number(id)))
    )
  );
}