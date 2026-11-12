import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ThemeCard } from './theme-card';
import { Theme } from '../../core/models/theme';

describe('ThemeCard', () => {
  let fixture: ComponentFixture<ThemeCard>;

  const theme: Theme = {
    id: 1,
    title: 'Angular',
    description: 'Discussions autour d’Angular',
    subscribed: false,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ThemeCard],
    }).compileComponents();

    fixture = TestBed.createComponent(ThemeCard);
    fixture.componentRef.setInput('theme', theme);
    fixture.componentRef.setInput('actionLabel', 'S’abonner');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should display the theme details and action label', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('mat-card-title')?.textContent).toContain('Angular');
    expect(element.querySelector('mat-card-content p')?.textContent)
      .toContain('Discussions autour d’Angular');
    expect(element.querySelector('button')?.textContent).toContain('S’abonner');
  });

  it('should emit the theme when the action button is clicked', () => {
    const emitSpy = jest.spyOn(fixture.componentInstance.action, 'emit');
    const button = (fixture.nativeElement as HTMLElement).querySelector('button')!;

    button.click();

    expect(emitSpy).toHaveBeenCalledWith(theme);
  });

  it('should disable the action button when actionDisabled is true', () => {
    fixture.componentRef.setInput('actionDisabled', true);
    fixture.detectChanges();

    const button = (fixture.nativeElement as HTMLElement).querySelector('button');

    expect(button?.disabled).toBe(true);
  });
});