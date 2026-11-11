describe('Register', () => {
  const USERNAME = 'input[autocomplete="username"]';
  const EMAIL = 'input[autocomplete="email"]';
  const PASSWORD = 'input[autocomplete="new-password"]';

  const fillRegister = (username: string, email: string, password: string) => {
    cy.typeInField(USERNAME, username);
    cy.typeInField(EMAIL, email);
    cy.typeInField(PASSWORD, password);
  };

  beforeEach(() => {
    cy.visit('/');
    cy.contains('a', "S'inscrire").click();
    cy.url().should('include', '/register');
  });

  it('registers with valid data and redirects to /articles', () => {
    cy.intercept('POST', '**/api/auth/register', {
      statusCode: 200,
      body: { token: 'fake-jwt-token' },
    }).as('register');

    cy.intercept('GET', '**/api/articles*', []).as('getArticles');

    fillRegister('john', 'john@test.com', 'Password1!');
    cy.contains('button', "S'inscrire").click();

    cy.wait('@register').its('request.body').should('deep.equal', {
      username: 'john',
      email: 'john@test.com',
      password: 'Password1!',
    });
    cy.url().should('include', '/articles');
    cy.window().its('localStorage.token').should('eq', 'fake-jwt-token');
  });

  it('shows an error message when the email is already used (409)', () => {
    cy.intercept('POST', '**/api/auth/register', {
      statusCode: 409,
      body: { message: 'Email already used' },
    }).as('register');

    fillRegister('john', 'john@test.com', 'Password1!');
    cy.contains('button', "S'inscrire").click();

    cy.wait('@register');
    cy.get('p.error[role="alert"]').should('contain', 'Une erreur est survenue');
    cy.url().should('include', '/register');
    cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');
  });

  it('disables the submit button when the form is empty', () => {
    cy.contains('button', "S'inscrire").should('be.disabled');
  });

  it('enables the submit button when all fields are valid', () => {
    fillRegister('john', 'john@test.com', 'Password1!');
    cy.contains('button', "S'inscrire").should('be.enabled');
  });

  it('shows required errors when fields are touched and left empty', () => {
    cy.get(USERNAME).focus().blur();
    cy.get(EMAIL).focus().blur();
    cy.get(PASSWORD).focus().blur();

    cy.contains('mat-error', "Nom d'utilisateur requis").should('be.visible');
    cy.contains('mat-error', 'Email requis').should('be.visible');
    cy.contains('mat-error', 'Mot de passe requis').should('be.visible');
  });

  it('shows an error for an invalid email', () => {
    cy.typeInField(EMAIL, 'not-an-email');
    cy.get(EMAIL).blur();

    cy.contains('mat-error', 'Email invalide').should('be.visible');
    cy.contains('button', "S'inscrire").should('be.disabled');
  });

  it('shows an error when the password is too short', () => {
    cy.typeInField(PASSWORD, 'Ab1!');
    cy.get(PASSWORD).blur();

    cy.contains('mat-error', '8 caractères minimum').should('be.visible');
  });

  it('shows an error when the password does not meet the complexity rules', () => {
    cy.typeInField(PASSWORD, 'password123');
    cy.get(PASSWORD).blur();

    cy.contains(
      'mat-error',
      'Une minuscule, une majuscule, un chiffre et un caractère spécial',
    ).should('be.visible');
  });

  it('goes back to the home page with the back button', () => {
    cy.get('a[aria-label="Retour"]').click();
    cy.url().should('eq', Cypress.config('baseUrl') + '/');
  });
});