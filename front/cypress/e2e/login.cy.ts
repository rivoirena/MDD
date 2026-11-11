describe('Login', () => {
  const IDENTIFIER = 'input[autocomplete="username"]';
  const PASSWORD = 'input[autocomplete="current-password"]';

  beforeEach(() => {
    cy.visit('/');
  });

  it('logs in with valid credentials and redirects to /articles', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 200,
      body: { token: 'fake-jwt-token' },
    }).as('login');

    cy.intercept('GET', '**/api/articles*', []).as('getArticles');

    cy.contains('a', 'Se connecter').click();
    cy.url().should('include', '/login');

    cy.typeInField(IDENTIFIER, 'john@test.com');
    cy.typeInField(PASSWORD, 'Password1!');
    cy.contains('button', 'Se connecter').click();

    cy.wait('@login');
    cy.url().should('include', '/articles');
    cy.window().its('localStorage.token').should('eq', 'fake-jwt-token');
  });

  it('shows an error message when credentials are invalid', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 401,
      body: { message: 'Unauthorized' },
    }).as('login');

    cy.contains('a', 'Se connecter').click();

    cy.typeInField(IDENTIFIER, 'john@test.com');
    cy.typeInField(PASSWORD, 'wrong');
    cy.contains('button', 'Se connecter').click();

    cy.wait('@login');
    cy.get('p.error[role="alert"]').should('contain', 'Identifiants incorrects.');
    cy.url().should('include', '/login');
    cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');
  });

  it('disables the submit button when the form is empty', () => {
    cy.contains('a', 'Se connecter').click();
    cy.contains('button', 'Se connecter').should('be.disabled');
  });

  it('enables the submit button once both fields are filled', () => {
    cy.contains('a', 'Se connecter').click();

    cy.typeInField(IDENTIFIER, 'john');
    cy.typeInField(PASSWORD, 'Password1!');

    cy.contains('button', 'Se connecter').should('be.enabled');
  });

  it('goes back to the home page with the back button', () => {
    cy.contains('a', 'Se connecter').click();
    cy.get('a[aria-label="Retour"]').click();
    cy.url().should('eq', Cypress.config('baseUrl') + '/');
  });
});