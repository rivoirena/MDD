import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { SubscriptionApi } from './subscription-api';
import { Theme } from '../models/theme';

describe('SubscriptionApi', () => {
  let service: SubscriptionApi;
  let httpTestingController: HttpTestingController;

  const baseUrl = 'http://localhost:8080/api/users/me/subscriptions';

  const theme: Theme = {
    id: 1,
    title: 'Java',
    description: 'Programmation Java',
    subscribed: true,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SubscriptionApi,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(SubscriptionApi);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get subscriptions', () => {
    service.getSubscriptions().subscribe((subscriptions) => {
      expect(subscriptions).toEqual([theme]);
    });

    const request = httpTestingController.expectOne(baseUrl);

    expect(request.request.method).toBe('GET');
    request.flush([theme]);
  });

  it('should subscribe to a theme', () => {
    const themeId = 1;

    service.subscribe(themeId).subscribe();

    const request = httpTestingController.expectOne(`${baseUrl}/${themeId}`);

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toBeNull();
    request.flush(null);
  });

  it('should unsubscribe from a theme', () => {
    const themeId = 1;

    service.unsubscribe(themeId).subscribe();

    const request = httpTestingController.expectOne(`${baseUrl}/${themeId}`);

    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});