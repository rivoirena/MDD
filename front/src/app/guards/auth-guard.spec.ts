import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';
import { Auth } from '../core/services/auth';
import { authGuard } from './auth-guard';

describe('authGuard', () => {
  let authService: { isLoggedIn: jest.Mock };
  let router: Router;

  beforeEach(() => {
    authService = {
      isLoggedIn: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: Auth, useValue: authService },
      ],
    });

    router = TestBed.inject(Router);
  });

  function executeGuard() {
    return TestBed.runInInjectionContext(() => authGuard({} as never, {} as never));
  }

  it('allows navigation when the user is logged in', () => {
    authService.isLoggedIn.mockReturnValue(true);

    expect(executeGuard()).toBe(true);
  });

  it('redirects to /articles when the user is logged in', () => {
    authService.isLoggedIn.mockReturnValue(false);

    const result = executeGuard() as UrlTree;

    expect(router.serializeUrl(result)).toBe('/login');
  });
});