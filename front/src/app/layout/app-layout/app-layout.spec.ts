import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideRouter, Router } from '@angular/router';
import { MatSidenavHarness } from '@angular/material/sidenav/testing';
import { Auth } from '../../core/services/auth';
import { AppLayout } from './app-layout';

describe('AppLayout', () => {
  let fixture: ComponentFixture<AppLayout>;
  let loader: HarnessLoader;
  let auth: { logout: jest.Mock };
  let router: Router;

  beforeEach(async () => {
    auth = { logout: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [AppLayout],
      providers: [
        provideRouter([]),
        { provide: Auth, useValue: auth },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AppLayout);
    router = TestBed.inject(Router);
    loader = TestbedHarnessEnvironment.loader(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should open the sidenav when the header menu button is clicked', async () => {
    const sidenav = await loader.getHarness(MatSidenavHarness);
    const menuButton = (fixture.nativeElement as HTMLElement)
      .querySelector<HTMLButtonElement>('app-app-header button.burger')!;

    expect(await sidenav.isOpen()).toBe(false);

    menuButton.click();

    expect(await sidenav.isOpen()).toBe(true);
  });

  it('should log out and navigate home when logout is called', () => {
    const navigateSpy = jest.spyOn(router, 'navigateByUrl');

    fixture.componentInstance.logout();

    expect(auth.logout).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith('/');
  });
});