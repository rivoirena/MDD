import { Component, inject, input, signal } from '@angular/core';
import { rxResource, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { ArticleApi } from '../../core/services/article-api';
import { CommentApi } from '../../core/services/comment-api';
import { DatePipe } from '@angular/common';
import { form, FormField, required, submit } from '@angular/forms/signals';

@Component({
  selector: 'app-article-detail',
  imports: [DatePipe, FormField],
  templateUrl: './article-detail.html',
  styleUrl: './article-detail.scss',
})
export class ArticleDetail {
  private articleService = inject(ArticleApi);
  private commentService = inject(CommentApi);

  id = input.required<string>();

  article = toSignal(
    toObservable(this.id).pipe(
      switchMap((id) => this.articleService.getArticleById(Number(id)))
    )
  );

  comments = rxResource({
    params: () => Number(this.id()),
    stream: ({ params }) => this.commentService.getCommentsByArticleId(params),
  });

  model = signal({ content: '' });

  commentForm = form(this.model, (p) => {
    required(p.content, { message: 'Le commentaire ne peut pas ĂŞtre vide' });
  });

  onSubmit(event: Event) {
    event.preventDefault();

    submit(this.commentForm, async () => {
      const { content } = this.model();

      return new Promise<void>((resolve) => {
        this.commentService.createComment(Number(this.id()), content).subscribe({
          next: () => {
            this.model.set({ content: '' });
            this.commentForm().reset();
            this.comments.reload();
            resolve();
          },
          error: () => resolve(),
        });
      });
    });
  }
}