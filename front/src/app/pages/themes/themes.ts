import { Component, inject } from '@angular/core';
import { ThemeApi } from '../../core/services/theme-api';
import { toSignal } from '@angular/core/rxjs-interop';
import { ThemeCard } from '../../components/theme-card/theme-card';

@Component({
  imports: [ThemeCard],
  selector: 'app-themes',
  styleUrl: './themes.scss',
  templateUrl: './themes.html',
})
export class Themes {
  private themeApi = inject(ThemeApi);

  themes = toSignal(this.themeApi.getAll(), { initialValue: [] });
}
