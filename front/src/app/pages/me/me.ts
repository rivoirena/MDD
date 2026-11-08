import { Component, computed, inject, linkedSignal, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { form, FormField, required, email, pattern, validate } from '@angular/forms/signals';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { UserApi } from '../../core/services/user-api';
import { ThemeCard } from '../../components/theme-card/theme-card';
import { SubscriptionApi } from '../../core/services/subscription-api';
import { Theme } from '../../core/models/theme';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-me',
  imports: [
    MatButtonModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    FormField,
    ThemeCard,
  ],
  templateUrl: './me.html',
  styleUrl: './me.scss',
})
export class Me {
  private userApi = inject(UserApi);
  private subscriptionApi = inject(SubscriptionApi);

  user = rxResource({
    stream: () => this.userApi.loadProfile(),
  });

  subscriptions = rxResource({
    stream: () => this.subscriptionApi.getSubscriptions(),
    defaultValue: [],
  });

  model = linkedSignal(() => ({
    username: this.user.value()?.username ?? '',
    email: this.user.value()?.email ?? '',
    password: '',
  }));

  hasChanges = computed(() => {
    const current = this.user.value();
    const { username, email, password } = this.model();
    return username !== current?.username || email !== current?.email || password !== '';
  });

  errorMessage = signal('');
  successMessage = signal('');

  profileForm = form(this.model, (p) => {
    required(p.username, { message: "Nom d'utilisateur requis" });
    pattern(p.username, /^[^@\s]+$/, { message: "Pas de @ ni d'espace" });

    required(p.email, { message: 'Email requis' });
    email(p.email, { message: 'Email invalide' });

    validate(p.password, ({ value }) => {
      const v = value();
      if (!v) return null;
      const ok = v.length >= 8 && /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/.test(v);
      return ok
        ? null
        : {
            kind: 'password',
            message:
              '8 caractères min., une minuscule, une majuscule, un chiffre et un caractère spécial',
          };
    });
  });

  save(event: Event): void {
    event.preventDefault();
    if (this.profileForm().invalid() || !this.hasChanges()) return;

    const { username, email, password } = this.model();
    const current = this.user.value();

    this.errorMessage.set('');
    this.successMessage.set('');

    this.userApi
      .updateProfile({
        username: username !== current?.username ? username : undefined,
        email: email !== current?.email ? email : undefined,
        password: password || undefined,
      })
      .subscribe({
        next: () => {
          this.successMessage.set('Profil mis à jour');
          this.user.reload();
        },
        error: (err) =>
          this.errorMessage.set(err.error?.message ?? 'Erreur lors de la mise à jour'),
      });
  }

  unsubscribe(theme: Theme): void {
    this.subscriptionApi.unsubscribe(theme.id).subscribe({
      next: () => this.subscriptions.reload(),
    });
  }
}
