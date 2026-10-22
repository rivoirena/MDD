import { Component, inject, input } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { ArticleService } from '../../core/services/article.service';
import { CommentService } from '../../core/services/comment.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-article-detail',
  imports: [DatePipe],
  templateUrl: './article-detail.html',
  styleUrl: './article-detail.scss',
})
export class ArticleDetail {
  private articleService = inject(ArticleService);
  private commentService = inject(CommentService);

  id = input.required<string>();

  article = toSignal(
    toObservable(this.id).pipe(
      switchMap((id) => this.articleService.getArticleById(Number(id)))
    )
  );

  comments = toSignal(
    toObservable(this.id).pipe(
      switchMap((id) => this.commentService.getCommentsByArticleId(Number(id)))
    ),
    { initialValue: [] }
  );
}