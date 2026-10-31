import { Component, inject, signal } from '@angular/core';
import { ThemeApi } from '../../core/services/theme-api';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ThemeCard } from '../../components/theme-card/theme-card';
import { Theme } from '../../core/models/theme';
import { SubscriptionApi } from '../../core/services/subsciption-api';
import { switchMap } from 'rxjs';

@Component({
  imports: [ThemeCard],
  selector: 'app-themes',
  styleUrl: './themes.scss',
  templateUrl: './themes.html',
})
export class Themes {
  private themeApi = inject(ThemeApi);
  private subscriptionApi = inject(SubscriptionApi);

  private refresh = signal(0);

  themes = toSignal(
    toObservable(this.refresh).pipe(switchMap(() => this.themeApi.getAll())),
    { initialValue: [] },
  );


  subscribe(theme: Theme): void {
    this.subscriptionApi.subscribe(theme.id).subscribe({
      next: () => this.refresh.update((n) => n + 1),
    });
  }
}
