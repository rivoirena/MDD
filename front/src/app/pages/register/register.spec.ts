import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, RouterLink } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Auth } from '../../core/services/auth';
import { Register } from './register';

describe('Register', () => {
  let fixture: ComponentFixture<Register>;
  let auth: { register: jest.Mock };
  let router: { navigate: jest.Mock };

  beforeEach(async () => {
    auth = { register: jest.fn() };
    router = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [
        { provide: Auth, useValue: auth },
        { provide: Router, useValue: router },
      ],
    })
      .overrideComponent(Register, {
        remove: { imports: [RouterLink] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(Register);
    fixture.detectChanges();
  });

  it('should not call Auth when the form is invalid', () => {
    fixture.componentInstance.submit(new Event('submit'));

    expect(auth.register).not.toHaveBeenCalled();
  });

  it('should call Auth with the form values when the form is valid', () => {
    const request = {
      username: 'jean',
      email: 'jean@example.com',
      password: 'MotDePasse1!',
    };
    auth.register.mockReturnValue(of({ token: 'fake-token' }));
    fixture.componentInstance.model.set(request);

    fixture.componentInstance.submit(new Event('submit'));

    expect(auth.register).toHaveBeenCalledWith(request);
  });

  it('should navigate to articles after successful registration', () => {
    auth.register.mockReturnValue(of({ token: 'fake-token' }));
    fixture.componentInstance.model.set({
      username: 'jean',
      email: 'jean@example.com',
      password: 'MotDePasse1!',
    });

    fixture.componentInstance.submit(new Event('submit'));

    expect(router.navigate).toHaveBeenCalledWith(['/articles']);
  });

  it('should set onError when registration fails', () => {
    auth.register.mockReturnValue(
      throwError(() => new Error('Erreur serveur')),
    );
    fixture.componentInstance.model.set({
      username: 'jean',
      email: 'jean@example.com',
      password: 'MotDePasse1!',
    });

    fixture.componentInstance.submit(new Event('submit'));

    expect(fixture.componentInstance.onError()).toBe(true);
  });
});