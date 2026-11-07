import { Component } from '@angular/core';
import { PublicHeader } from '../public-header/public-header';
import { RouterOutlet } from '@angular/router';

@Component({
  imports: [RouterOutlet,PublicHeader],
  selector: 'app-public-layout',
  styleUrl: './public-layout.scss',
  templateUrl: './public-layout.html',
})
export class PublicLayout {}
