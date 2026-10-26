import { Component, input, output } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { Theme } from '../../core/models/theme';

@Component({
  selector: 'app-theme-card',
  imports: [MatCardModule, MatButtonModule],
  templateUrl: './theme-card.html',
  styleUrl: './theme-card.scss',
})
export class ThemeCard {
  theme = input.required<Theme>();
  actionLabel = input<string>();
  action = output<Theme>();
}