import { Routes } from '@angular/router';
import { ArticleList } from './pages/article-list/article-list';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { ArticleDetail } from './pages/article-detail/article-detail';

export const routes: Routes = [
  { path: 'articles', component: ArticleList },
  { path: '', redirectTo: 'articles', pathMatch: 'full' },
  { path: 'articles/:id', component: ArticleDetail },
];