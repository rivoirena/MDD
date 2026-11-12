import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';

import { UserApi } from './user-api';
import { User } from '../models/user';
import { UpdateProfileRequest } from '../models/update-profile-request';
import { JwtResponse } from '../models/jwt-response';

describe('UserApi', () => {
  let service: UserApi;
  let httpTestingController: HttpTestingController;

  const baseUrl = 'http://localhost:8080/api/users';

  const user: User = {
    email: 'alice@example.com',
    username: 'alice',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });

    service = TestBed.inject(UserApi);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    localStorage.clear();
  });

  it('should load the profile and update currentUser', () => {
    service.loadProfile().subscribe((result) => {
      expect(result).toEqual(user);
      expect(service.currentUser()).toEqual(user);
    });

    const request = httpTestingController.expectOne(`${baseUrl}/me`);

    expect(request.request.method).toBe('GET');
    request.flush(user);
  });

  it('should update the profile, save the token, and leave currentUser unchanged', () => {
    service.currentUser.set(user);

    const profileRequest: UpdateProfileRequest = {
      username: 'alice-updated',
    };

    const response: JwtResponse = { token: 'new-jwt-token' };

    service.updateProfile(profileRequest).subscribe((result) => {
      expect(result).toEqual(response);
      expect(localStorage.getItem('token')).toBe('new-jwt-token');
      expect(service.currentUser()).toEqual(user);
    });

    const request = httpTestingController.expectOne(`${baseUrl}/me`);

    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(profileRequest);
    request.flush(response);
  });
});