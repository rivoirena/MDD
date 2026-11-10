import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, RouterLink } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Auth } from '../../core/services/auth';
import { Login } from './login';

describe('Login', () => {
  let fixture: ComponentFixture<Login>;
  let component: Login;

  let auth: { login: jest.Mock };
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    auth = { login: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        { provide: Auth, useValue: auth },
        { provide: Router, useValue: router },
      ],
    })
      .overrideComponent(Login, {
        remove: { imports: [RouterLink] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not call Auth when the form is invalid', () => {
    component.submit(new Event('submit'));

    expect(auth.login).not.toHaveBeenCalled();
  });

  it('should call Auth with the form values when the form is valid', () => {
    const credentials = {
      identifier: 'jean@example.com',
      password: 'MotDePasse1!',
    };
    auth.login.mockReturnValue(of({ token: 'fake-token' }));
    component.model.set(credentials);

    component.submit(new Event('submit'));

    expect(auth.login).toHaveBeenCalledWith(credentials);
  });

  it('should navigate to articles after a successful login', () => {
    auth.login.mockReturnValue(of({ token: 'fake-token' }));
    component.model.set({
      identifier: 'jean@example.com',
      password: 'MotDePasse1!',
    });

    component.submit(new Event('submit'));

    expect(router.navigate).toHaveBeenCalledWith(['/articles']);
  });

  it('should set onError when login fails', () => {
    auth.login.mockReturnValue(
      throwError(() => new Error('Identifiants invalides')),
    );
    component.model.set({
      identifier: 'jean@example.com',
      password: 'incorrect',
    });

    component.submit(new Event('submit'));

    expect(component.onError()).toBe(true);
  });
});