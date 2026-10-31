import { TestBed } from '@angular/core/testing';
import { SubsciptionApi } from './subsciption-api';

describe('SubsciptionApi', () => {
  let service: SubsciptionApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SubsciptionApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
