import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ThemeApi } from './theme-api';
import { Theme } from '../models/theme';
import { environment } from '../../../environments/environment';

describe('ThemeApi', () => {
  let service: ThemeApi;
  let httpTestingController: HttpTestingController;

  const expectedUrl = `${environment.apiUrl}/themes`;

  const themes: Theme[] = [
    {
      id: 1,
      title: 'Java',
      description: 'Programmation Java',
      subscribed: true,
    },
    {
      id: 2,
      title: 'Angular',
      description: 'Développement frontend',
      subscribed: false,
    },
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(ThemeApi);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all themes', () => {
    service.getAll().subscribe((result) => {
      expect(result).toEqual(themes);
    });

    const request = httpTestingController.expectOne(expectedUrl);

    expect(request.request.method).toBe('GET');
    request.flush(themes);
  });
});
