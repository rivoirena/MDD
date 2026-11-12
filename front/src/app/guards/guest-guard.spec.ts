import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';
import { Auth } from '../core/services/auth';
import { guestGuard } from './guest-guard';

describe('guestGuard', () => {
  let authService: { isLoggedIn: jest.Mock };
  let router: Router;

  beforeEach(() => {
    authService = {
      isLoggedIn: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: Auth, useValue: authService }],
    });

    router = TestBed.inject(Router);
  });

  function executeGuard() {
    return TestBed.runInInjectionContext(() => guestGuard({} as never, {} as never));
  }

  it('allows navigation when the user is not logged in', () => {
    authService.isLoggedIn.mockReturnValue(false);

    expect(executeGuard()).toBe(true);
  });

  it('redirects to /login when the user is not logged in', () => {
    authService.isLoggedIn.mockReturnValue(true);

    const result = executeGuard() as UrlTree;

    expect(router.serializeUrl(result)).toBe('/articles');
  });
});
