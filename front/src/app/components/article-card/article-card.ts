import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { Article } from '../../core/models/article.interface';

@Component({
  selector: 'app-article-card',
  imports: [MatCardModule, DatePipe],
  templateUrl: './article-card.html',
  styleUrl: './article-card.scss',
})

export class ArticleCard {
  article = input.required<Article>();
}