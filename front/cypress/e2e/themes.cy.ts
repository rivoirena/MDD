describe('Themes', () => {
  beforeEach(() => {
    cy.loginByToken();
  });

  it('displays all themes from the API', () => {
    cy.intercept('GET', '**/api/themes', { fixture: 'themes.json' }).as('getThemes');

    cy.visit('/themes');
    cy.wait('@getThemes');

    cy.get('app-theme-card').should('have.length', 3);
    cy.contains('app-theme-card', 'Angular').within(() => {
      cy.contains('Framework front-end de Google pour créer des applications web.');
    });
    cy.contains('app-theme-card', 'Spring Boot').should('be.visible');
    cy.contains('app-theme-card', 'TypeScript').should('be.visible');
  });

  it('shows "S\'abonner" for unsubscribed themes and "Déjà abonné" (disabled) for subscribed ones', () => {
    cy.intercept('GET', '**/api/themes', { fixture: 'themes.json' }).as('getThemes');

    cy.visit('/themes');
    cy.wait('@getThemes');

    cy.contains('app-theme-card', 'Angular').find('button').should('contain', "S'abonner").and('be.enabled');
    cy.contains('app-theme-card', 'TypeScript').find('button').should('contain', "S'abonner").and('be.enabled');
    cy.contains('app-theme-card', 'Spring Boot').find('button').should('contain', 'Déjà abonné').and('be.disabled');
  });

  it('subscribes to a theme and refreshes the list', () => {
    cy.fixture('themes.json').then((themes) => {
      const afterSubscribe = themes.map((t: { id: number; subscribed: boolean }) =>
        t.id === 1 ? { ...t, subscribed: true } : t,
      );

      let getCalls = 0;
      cy.intercept('GET', '**/api/themes', (req) => {
        getCalls++;
        req.reply(getCalls === 1 ? themes : afterSubscribe);
      }).as('getThemes');

      cy.intercept('POST', '**/api/users/me/subscriptions/1', { statusCode: 204 }).as('subscribe');

      cy.visit('/themes');
      cy.wait('@getThemes');

      cy.contains('app-theme-card', 'Angular').find('button').click();

      cy.wait('@subscribe');
      cy.wait('@getThemes');

      cy.contains('app-theme-card', 'Angular')
        .find('button')
        .should('contain', 'Déjà abonné')
        .and('be.disabled');
    });
  });

  it('does not change the list when the subscription fails', () => {
    cy.intercept('GET', '**/api/themes', { fixture: 'themes.json' }).as('getThemes');
    cy.intercept('POST', '**/api/users/me/subscriptions/1', {
      statusCode: 404,
      body: { message: 'Theme not found' },
    }).as('subscribe');

    cy.visit('/themes');
    cy.wait('@getThemes');

    cy.contains('app-theme-card', 'Angular').find('button').click();
    cy.wait('@subscribe');

    cy.contains('app-theme-card', 'Angular').find('button').should('contain', "S'abonner").and('be.enabled');
  });

  it('shows no theme card when the API returns an empty list', () => {
    cy.intercept('GET', '**/api/themes', []).as('getThemes');

    cy.visit('/themes');
    cy.wait('@getThemes');

    cy.get('app-theme-card').should('not.exist');
  });
});