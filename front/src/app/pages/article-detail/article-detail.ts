import { Component, inject, input, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
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
  private articleApi = inject(ArticleApi);
  private commentApi = inject(CommentApi);

  id = input.required<string>();

  article = rxResource({
    params: () => Number(this.id()),
    stream: ({ params }) => this.articleApi.getArticleById(params),
  });

  comments = rxResource({
    params: () => Number(this.id()),
    stream: ({ params }) => this.commentApi.getCommentsByArticleId(params),
    defaultValue: [],
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
        this.commentApi.createComment(Number(this.id()), content).subscribe({
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
