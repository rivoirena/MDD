import { Routes } from '@angular/router';
import { ArticleList } from './pages/article-list/article-list';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { ArticleDetail } from './pages/article-detail/article-detail';
import { Register } from './pages/register/register';
import { Login } from './pages/login/login';
import { authGuard } from './guards/auth-guard';
import { Me } from './pages/me/me';
import { Themes } from './pages/themes/themes';
import { ArticleCreate } from './pages/article-create/article-create';

export const routes: Routes = [ 
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'me', component: Me },
  { path: 'themes', component: Themes },
  { path: 'articles', component: ArticleList, canActivate: [authGuard]  },
  { path: '', redirectTo: 'articles', pathMatch: 'full' },
  { path: 'articles/new', component: ArticleCreate, canActivate: [authGuard]  },
  { path: 'articles/:id', component: ArticleDetail, canActivate: [authGuard]  },
];