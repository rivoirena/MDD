import { Routes } from '@angular/router';
import { ArticleList } from './pages/article-list/article-list';
import { ArticleDetail } from './pages/article-detail/article-detail';
import { Register } from './pages/register/register';
import { Login } from './pages/login/login';
import { authGuard } from './guards/auth-guard';
import { Me } from './pages/me/me';
import { Themes } from './pages/themes/themes';
import { ArticleCreate } from './pages/article-create/article-create';
import { Home } from './pages/home/home';
import { PublicLayout } from './layout/public-layout/public-layout';
import { AppLayout } from './layout/app-layout/app-layout';



export const routes: Routes = [
  { path: '', component: Home, pathMatch: 'full' },
  {
    path: '',
    component: PublicLayout,
    children: [
      { path: 'login', component: Login },
      { path: 'register', component: Register },
    ],
  },
  {
    path: '',
    component: AppLayout,
    canActivate: [authGuard],
    children: [
      { path: 'articles', component: ArticleList },
      { path: 'articles/new', component: ArticleCreate },
      { path: 'articles/:id', component: ArticleDetail },
      { path: 'themes', component: Themes },
      { path: 'me', component: Me },
    ],
  },
];