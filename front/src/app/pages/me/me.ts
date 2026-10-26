import { Component, computed, inject, linkedSignal, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { form, FormField, required, email, pattern, validate } from '@angular/forms/signals';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { UserApi } from '../../core/services/user-api';

@Component({
  selector: 'app-me',
  imports: [FormField, MatFormFieldModule, MatInputModule, MatButtonModule],
  templateUrl: './me.html',
  styleUrl: './me.scss',
})
export class Me {
  private userApi = inject(UserApi);

  private refresh = signal(0);

  user = toSignal(toObservable(this.refresh).pipe(switchMap(() => this.userApi.loadProfile())));

  // Se réinitialise automatiquement à chaque nouveau profil chargé
  model = linkedSignal(() => ({
    username: this.user()?.username ?? '',
    email: this.user()?.email ?? '',
    password: '',
  }));

  hasChanges = computed(() => {
    const current = this.user();
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
    const current = this.user();

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
          this.refresh.update((n) => n + 1); // recharge le profil
        },
        error: (err) =>
          this.errorMessage.set(err.error?.message ?? 'Erreur lors de la mise à jour'),
      });
  }
}
