import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { Auth } from '../../core/services/auth';
import { AppHeader } from './app-header';

describe('AppHeader', () => {
  let fixture: ComponentFixture<AppHeader>;
  let auth: { logout: jest.Mock };
  let router: Router;

  beforeEach(async () => {
    auth = { logout: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [AppHeader],
      providers: [
        provideRouter([]),
        { provide: Auth, useValue: auth },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppHeader);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should emit menuToggle when the burger button is clicked', () => {
    const emitSpy = jest.spyOn(fixture.componentInstance.menuToggle, 'emit');
    const button = (fixture.nativeElement as HTMLElement)
      .querySelector<HTMLButtonElement>('button.burger')!;

    button.click();

    expect(emitSpy).toHaveBeenCalled();
  });

  it('should log out and navigate to the home page', () => {
    const navigateSpy = jest.spyOn(router, 'navigateByUrl');

    fixture.componentInstance.logout();

    expect(auth.logout).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith('/');
  });
});