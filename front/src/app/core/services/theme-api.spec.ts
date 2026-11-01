import { TestBed } from '@angular/core/testing';
import { ThemeApi } from './theme-api';

describe('ThemeApi', () => {
  let service: ThemeApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ThemeApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
