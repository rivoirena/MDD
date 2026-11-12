import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { UserApi } from '../../core/services/user-api';
import { SubscriptionApi } from '../../core/services/subscription-api';
import { Theme } from '../../core/models/theme';
import { Me } from './me';

describe('Me', () => {
  let fixture: ComponentFixture<Me>;
  let component: Me;

  let userApi: {
    loadProfile: jest.Mock;
    updateProfile: jest.Mock;
  };
  let subscriptionApi: {
    getSubscriptions: jest.Mock;
    unsubscribe: jest.Mock;
  };

  const profile = {
    id: 1,
    username: 'jean',
    email: 'jean@example.com',
  };

  beforeEach(async () => {
    userApi = {
      loadProfile: jest.fn().mockReturnValue(of(profile)),
      updateProfile: jest.fn(),
    };

    subscriptionApi = {
      getSubscriptions: jest.fn().mockReturnValue(of([])),
      unsubscribe: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [Me],
      providers: [
        { provide: UserApi, useValue: userApi },
        { provide: SubscriptionApi, useValue: subscriptionApi },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Me);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load the profile and subscriptions', () => {
    expect(userApi.loadProfile).toHaveBeenCalled();
    expect(subscriptionApi.getSubscriptions).toHaveBeenCalled();
    expect(component.user.value()).toEqual(profile);
  });

  it('should not update the profile when there are no changes', () => {
    component.save(new Event('submit'));

    expect(userApi.updateProfile).not.toHaveBeenCalled();
  });

  it('should update only the changed profile fields', () => {
    userApi.updateProfile.mockReturnValue(of({ token: 'fake-token' }));

    component.model.update((value) => ({
      ...value,
      username: 'nouveau-nom',
    }));

    component.save(new Event('submit'));

    expect(userApi.updateProfile).toHaveBeenCalledWith({
      username: 'nouveau-nom',
      email: undefined,
      password: undefined,
    });
    expect(component.successMessage()).toBe('Profil mis à jour');
  });

  it('should display the API error when the update fails', () => {
    userApi.updateProfile.mockReturnValue(
      throwError(() => ({ error: { message: 'Email déjà utilisé' } })),
    );

    component.model.update((value) => ({
      ...value,
      email: 'autre@example.com',
    }));

    component.save(new Event('submit'));

    expect(component.errorMessage()).toBe('Email déjà utilisé');
    expect(component.successMessage()).toBe('');
  });

  it('should use a fallback message when the update error has no message', () => {
    userApi.updateProfile.mockReturnValue(
      throwError(() => ({ error: {} })),
    );

    component.model.update((value) => ({
      ...value,
      username: 'nouveau-nom',
    }));

    component.save(new Event('submit'));

    expect(component.errorMessage()).toBe('Erreur lors de la mise à jour');
  });

  it('should unsubscribe from a theme and reload subscriptions', () => {
    subscriptionApi.unsubscribe.mockReturnValue(of(void 0));
    const reloadSpy = jest.spyOn(component.subscriptions, 'reload');

    component.unsubscribe({ id: 42 } as Theme);

    expect(subscriptionApi.unsubscribe).toHaveBeenCalledWith(42);
    expect(reloadSpy).toHaveBeenCalled();
  });

  it('should accept a password meeting all requirements', () => {
  component.model.update((model) => ({
    ...model,
    password: 'Abcd123!',
  }));

  expect(component.profileForm.password().valid()).toBe(true);
});

it('should reject a password missing a required character type', () => {
  component.model.update((model) => ({
    ...model,
    password: 'abcdefgh',
  }));

  expect(component.profileForm.password().valid()).toBe(false);
});
});