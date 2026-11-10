import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { Theme } from '../../core/models/theme';
import { SubscriptionApi } from '../../core/services/subscription-api';
import { ThemeApi } from '../../core/services/theme-api';
import { Themes } from './themes';

describe('Themes', () => {
  let fixture: ComponentFixture<Themes>;
  let component: Themes;

  let themeApi: { getAll: jest.Mock };
  let subscriptionApi: { subscribe: jest.Mock };

  const themes: Theme[] = [
    {
      id: 1,
      title: 'Angular',
      description: 'Développement avec Angular',
      subscribed: false,
    },
    {
      id: 2,
      title: 'Java',
      description: 'Développement avec Java',
      subscribed: true,
    },
  ];

  beforeEach(async () => {
    themeApi = {
      getAll: jest.fn().mockReturnValue(of(themes)),
    };

    subscriptionApi = {
      subscribe: jest.fn().mockReturnValue(of(void 0)),
    };

    await TestBed.configureTestingModule({
      imports: [Themes],
      providers: [
        { provide: ThemeApi, useValue: themeApi },
        { provide: SubscriptionApi, useValue: subscriptionApi },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Themes);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('should create and load themes', () => {
    expect(component).toBeTruthy();
    expect(themeApi.getAll).toHaveBeenCalled();
    expect(component.themes.value()).toEqual(themes);
  });

  it('should display the themes', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent;

    expect(text).toContain('Angular');
    expect(text).toContain('Java');
    expect(text).toContain("S'abonner");
    expect(text).toContain('Déjà abonné');
  });

  it('should subscribe to a theme and reload the themes', () => {
    const reloadSpy = jest.spyOn(component.themes, 'reload');

    component.subscribe(themes[0]);

    expect(subscriptionApi.subscribe).toHaveBeenCalledWith(1);
    expect(reloadSpy).toHaveBeenCalled();
  });

  it('should not subscribe when the subscription request fails', () => {
    subscriptionApi.subscribe.mockReturnValue(
      // L’erreur ne déclenche pas le callback next.
      of(void 0),
    );

    component.subscribe(themes[1]);

    expect(subscriptionApi.subscribe).toHaveBeenCalledWith(2);
  });
});