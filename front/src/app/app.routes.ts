import { Routes } from '@angular/router';
import { ArticleList } from './pages/article-list/article-list';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { ArticleDetail } from './pages/article-detail/article-detail';
import { Register } from './pages/register/register';
import { Login } from './pages/login/login';

export const routes: Routes = [ 
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'articles', component: ArticleList },
  { path: '', redirectTo: 'articles', pathMatch: 'full' },
  { path: 'articles/:id', component: ArticleDetail },
];