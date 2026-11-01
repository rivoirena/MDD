import { Component, inject, input, numberAttribute, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { ArticleApi } from '../../core/services/article-api';
import { CommentApi } from '../../core/services/comment-api';
import { DatePipe } from '@angular/common';
import { form, FormField, required, submit } from '@angular/forms/signals';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-article-detail',
  imports: [DatePipe, FormField],
  templateUrl: './article-detail.html',
  styleUrl: './article-detail.scss',
})
export class ArticleDetail {
  private articleApi = inject(ArticleApi);
  private commentApi = inject(CommentApi);

  id = input.required<number, string>({ transform: numberAttribute });

  article = rxResource({
    params: () => this.id(),
    stream: ({ params }) => this.articleApi.getArticleById(params),
  });

  comments = rxResource({
    params: () => this.id(),
    stream: ({ params }) => this.commentApi.getCommentsByArticleId(params),
    defaultValue: [],
  });

  model = signal({ content: '' });

  commentForm = form(this.model, (p) => {
    required(p.content, { message: 'Le commentaire ne peut pas être vide' });
  });

  onSubmit(event: Event) {
    event.preventDefault();

    submit(this.commentForm, async () => {
      await firstValueFrom(this.commentApi.createComment(this.id(), this.model().content));
      this.commentForm().reset({ content: '' });
      this.comments.reload();
    });
  }
}
