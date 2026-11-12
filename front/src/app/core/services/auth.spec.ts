import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { Auth } from './auth';
import { RegisterRequest } from '../models/register-request';
import { LoginRequest } from '../models/login-request';
import { JwtResponse } from '../models/jwt-response';
import { environment } from '../../../environments/environment';

describe('Auth', () => {
  let service: Auth;
  let httpTestingController: HttpTestingController;

  const baseUrl = `${environment.apiUrl}/auth`;

  const registerRequest: RegisterRequest = {
    email: 'alice@example.com',
    username: 'Alice',
    password: 'Password123!',
  };

  const loginRequest: LoginRequest = {
    identifier: 'alice@example.com',
    password: 'Password123!',
  };

  const jwtResponse: JwtResponse = {
    token: 'test-jwt-token',
  };

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [
        Auth,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(Auth);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('register', () => {
    it('should send the registration request and save the returned token', () => {
      service.register(registerRequest).subscribe((response) => {
        expect(response).toEqual(jwtResponse);
        expect(service.getToken()).toBe(jwtResponse.token);
      });

      const request = httpTestingController.expectOne(
        `${baseUrl}/register`,
      );

      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual(registerRequest);

      request.flush(jwtResponse);
    });
  });

  describe('login', () => {
    it('should send the login request and save the returned token', () => {
      service.login(loginRequest).subscribe((response) => {
        expect(response).toEqual(jwtResponse);
        expect(service.getToken()).toBe(jwtResponse.token);
      });

      const request = httpTestingController.expectOne(
        `${baseUrl}/login`,
      );

      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual(loginRequest);

      request.flush(jwtResponse);
    });
  });

  describe('getToken', () => {
    it('should return null when no token is stored', () => {
      expect(service.getToken()).toBeNull();
    });

    it('should return the stored token', () => {
      localStorage.setItem('token', jwtResponse.token);

      expect(service.getToken()).toBe(jwtResponse.token);
    });
  });

  describe('isLoggedIn', () => {
    it('should return false when no token is stored', () => {
      expect(service.isLoggedIn()).toBe(false);
    });

    it('should return true when a token is stored', () => {
      localStorage.setItem('token', jwtResponse.token);

      expect(service.isLoggedIn()).toBe(true);
    });
  });

  describe('logout', () => {
    it('should remove the stored token', () => {
      localStorage.setItem('token', jwtResponse.token);

      service.logout();

      expect(service.getToken()).toBeNull();
      expect(service.isLoggedIn()).toBe(false);
    });
  });
});