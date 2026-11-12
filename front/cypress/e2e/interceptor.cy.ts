describe('Auth interceptor', () => {
  it('logs out and redirects to /login when the API returns 401', () => {
    cy.loginByToken();
    cy.intercept('GET', '**/api/themes', { statusCode: 401, body: {} }).as('getThemes');
    cy.intercept('GET', '**/api/articles*', { fixture: 'articles.json' });
    cy.visit('/themes');
    cy.wait('@getThemes');
    cy.url().should('include', '/login');
    cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');
  });

  it('does not log out on 401 from /auth/login', () => {
    cy.intercept('POST', '**/api/auth/login', {
      statusCode: 401,
      body: { message: 'Bad credentials' },
    }).as('login');
    cy.visit('/login');
    cy.typeInField('input[autocomplete="username"]', 'john@test.com');
    cy.typeInField('input[autocomplete="current-password"]', 'wrong');
    cy.contains('button', 'Se connecter').click();
    cy.wait('@login');
    cy.url().should('include', '/login');
  });
});
