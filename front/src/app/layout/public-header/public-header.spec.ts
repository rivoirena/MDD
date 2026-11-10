import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PublicHeader } from './public-header';

describe('PublicHeader', () => {
  let fixture: ComponentFixture<PublicHeader>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PublicHeader],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(PublicHeader);
    fixture.detectChanges();
  });

  it('should display the logo linking to the home page', () => {
    const element = fixture.nativeElement as HTMLElement;
    const link = element.querySelector<HTMLAnchorElement>('a');
    const image = element.querySelector<HTMLImageElement>('img');

    expect(link).not.toBeNull();
    expect(link?.getAttribute('href')).toBe('/');
    expect(image?.getAttribute('alt')).toBe('MDD');
  });
});