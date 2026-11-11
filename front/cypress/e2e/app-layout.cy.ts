describe('App layout', () => {
  beforeEach(() => {
    cy.loginByToken();
    cy.intercept('GET', '**/api/articles*', { fixture: 'articles.json' }).as('getArticles');
    cy.intercept('GET', '**/api/themes', { fixture: 'themes.json' }).as('getThemes');
  });

  describe('desktop', () => {
    beforeEach(() => {
      cy.viewport(1280, 800);
      cy.visit('/articles');
      cy.wait('@getArticles');
    });

    it('shows the desktop navigation', () => {
      cy.get('nav.desktop').should('be.visible');
      cy.get('button.burger').should('not.be.visible');
    });

    it('navigates to the themes page', () => {
      cy.get('nav.desktop').contains('a', 'Thèmes').click();
      cy.url().should('include', '/themes');
    });

    it('navigates back to the articles page', () => {
      cy.get('nav.desktop').contains('a', 'Thèmes').click();
      cy.get('nav.desktop').contains('a', 'Articles').click();
      cy.url().should('include', '/articles');
    });

    it('navigates to the profile page', () => {
      cy.intercept('GET', '**/api/users/me', { email: 'john@test.com', username: 'john' });
      cy.intercept('GET', '**/api/users/me/subscriptions', []);

      cy.get('nav.desktop').find('a[aria-label="Mon profil"]').click();
      cy.url().should('include', '/me');
    });

    it('logs out and redirects to the home page', () => {
      cy.get('nav.desktop').contains('button', 'Se déconnecter').click();

      cy.url().should('eq', Cypress.config('baseUrl') + '/');
      cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');
      cy.contains('a', 'Se connecter').should('be.visible');
    });
  });

  describe('mobile', () => {
    beforeEach(() => {
      cy.viewport('iphone-x');
      cy.visit('/articles');
      cy.wait('@getArticles');
    });

    it('shows the burger button and hides the desktop navigation', () => {
      cy.get('button.burger').should('be.visible');
      cy.get('nav.desktop').should('not.be.visible');
    });

    it('opens the sidenav when the burger is clicked', () => {
      cy.get('button.burger').click();
      cy.get('mat-sidenav').should('be.visible');
      cy.get('mat-sidenav').contains('a', 'Thèmes').should('be.visible');
    });

    it('navigates to the themes page and closes the sidenav', () => {
      cy.get('button.burger').click();
      cy.get('mat-sidenav').contains('a', 'Thèmes').click();

      cy.url().should('include', '/themes');
      cy.get('mat-sidenav').should('not.be.visible');
    });

    it('logs out from the sidenav', () => {
      cy.get('button.burger').click();
      cy.get('mat-sidenav').contains('button', 'Se déconnecter').click();

      cy.url().should('eq', Cypress.config('baseUrl') + '/');
      cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');
    });
  });
});