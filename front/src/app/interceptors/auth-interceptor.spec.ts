import {
  HttpErrorResponse,
  HttpInterceptorFn,
  provideHttpClient,
  HttpClient,
  withInterceptors,
} from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Auth } from '../core/services/auth';
import { authInterceptor } from './auth-interceptor';

describe('authInterceptor', () => {
  let httpTestingController: HttpTestingController;
  let auth: Auth;
  let navigate: jest.Mock;

  beforeEach(() => {
    navigate = jest.fn();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: { navigate } },
      ],
    });

    httpTestingController = TestBed.inject(HttpTestingController);
    auth = TestBed.inject(Auth);
    localStorage.clear();
  });

  afterEach(() => {
    httpTestingController.verify();
    localStorage.clear();
  });

  it('adds the Bearer token when one is stored', () => {
    localStorage.setItem('token', 'test-token');

    TestBed.inject(HttpClient).get('/api/articles').subscribe();

    const request = httpTestingController.expectOne('/api/articles');

    expect(request.request.headers.get('Authorization')).toBe('Bearer test-token');

    request.flush([]);
  });

  it('does not add an Authorization header when no token is stored', () => {
    TestBed.inject(HttpClient).get('/api/articles').subscribe();

    const request = httpTestingController.expectOne('/api/articles');

    expect(request.request.headers.has('Authorization')).toBeFalsy();

    request.flush([]);
  });

  it('logs out and navigates to /login on a 401 outside an auth route', () => {
    localStorage.setItem('token', 'test-token');

    TestBed.inject(HttpClient)
      .get('/api/articles')
      .subscribe({
        error: () => {},
      });

    const request = httpTestingController.expectOne('/api/articles');
    request.flush(null, {
      status: 401,
      statusText: 'Unauthorized',
    });

    expect(localStorage.getItem('token')).toBeNull();
    expect(navigate).toHaveBeenCalledWith(['/login']);
  });

  it('does not log out or navigate on a 401 from an auth route', () => {
    localStorage.setItem('token', 'test-token');

    TestBed.inject(HttpClient)
      .post('/api/auth/login', {})
      .subscribe({
        error: () => {},
      });

    const request = httpTestingController.expectOne('/api/auth/login');
    request.flush(null, {
      status: 401,
      statusText: 'Unauthorized',
    });

    expect(localStorage.getItem('token')).toBe('test-token');
    expect(navigate).not.toHaveBeenCalled();
  });
});
