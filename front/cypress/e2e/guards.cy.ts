describe('Route guards', () => {
  it('redirects to /login when visiting a protected route without token', () => {
    cy.visit('/articles');
    cy.location('pathname').should('eq', '/');
  });

  it('redirects to /articles when visiting /login while logged in', () => {
    cy.loginByToken();
    cy.intercept('GET', '**/api/articles*', { fixture: 'articles.json' });
    cy.visit('/login');
    cy.url().should('match', /\/articles$/);
  });

  it('redirects to /articles when visiting the home page while logged in', () => {
    cy.loginByToken();
    cy.intercept('GET', '**/api/articles*', { fixture: 'articles.json' });
    cy.visit('/');
    cy.url().should('match', /\/articles$/);
  });
});
