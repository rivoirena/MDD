import { Component, inject } from '@angular/core';
import { ThemeApi } from '../../core/services/theme-api';
import { rxResource } from '@angular/core/rxjs-interop';
import { ThemeCard } from '../../components/theme-card/theme-card';
import { Theme } from '../../core/models/theme';
import { SubscriptionApi } from '../../core/services/subscription-api';

@Component({
  imports: [ThemeCard],
  selector: 'app-themes',
  styleUrl: './themes.scss',
  templateUrl: './themes.html',
})
export class Themes {
  private themeApi = inject(ThemeApi);
  private subscriptionApi = inject(SubscriptionApi);

  themes = rxResource({
    stream: () => this.themeApi.getAll(),
    defaultValue: [],
  });

  subscribe(theme: Theme): void {
    this.subscriptionApi.subscribe(theme.id).subscribe({
      next: () => this.themes.reload(),
    });
  }
}
