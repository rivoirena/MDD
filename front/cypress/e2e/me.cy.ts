describe('Me', () => {
  beforeEach(() => {
    cy.loginByToken();

    cy.intercept('GET', '**/api/users/me', { fixture: 'user.json' }).as('getProfile');

    cy.fixture('themes.json').then((themes: { subscribed: boolean }[]) => {
      cy.intercept(
        'GET',
        '**/api/users/me/subscriptions',
        themes.filter((t) => t.subscribed),
      ).as('getSubscriptions');
    });
  });

  const visitProfile = () => {
    cy.visit('/me');
    cy.wait(['@getProfile', '@getSubscriptions']);
  };

  it('pre-fills the form with the current user data', () => {
    visitProfile();

    cy.get('input[autocomplete="username"]').should('have.value', 'alice');
    cy.get('input[autocomplete="email"]').should('have.value', 'alice@test.com');
    cy.get('input[autocomplete="new-password"]').should('have.value', '');
  });

  it('disables the save button when nothing has changed', () => {
    visitProfile();

    cy.contains('button', 'Sauvegarder').should('be.disabled');
  });

  it('enables the save button once a field is modified', () => {
    visitProfile();

    cy.get('input[autocomplete="username"]').clear().type('alice2');

    cy.contains('button', 'Sauvegarder').should('be.enabled');
  });

  it('shows validation errors for invalid values', () => {
    visitProfile();

    cy.get('input[autocomplete="username"]').clear().blur();
    cy.contains("Nom d'utilisateur requis").should('be.visible');

    cy.get('input[autocomplete="email"]').clear().type('pas-un-email').blur();
    cy.contains('Email invalide').should('be.visible');

    cy.get('input[autocomplete="new-password"]').type('faible').blur();
    cy.contains('8 caractères min.').should('be.visible');
  });

  it('sends only the modified fields and shows a success message', () => {
    cy.intercept('PUT', '**/api/users/me', {
      statusCode: 200,
      body: { token: 'new-fake-jwt-token' },
    }).as('updateProfile');

    visitProfile();

    cy.get('input[autocomplete="username"]').clear().type('alice2');
    cy.contains('button', 'Sauvegarder').click();

    cy.wait('@updateProfile').its('request.body').should('deep.equal', {
      username: 'alice2',
    });
    cy.contains('Profil mis à jour').should('be.visible');
    cy.window().its('localStorage').invoke('getItem', 'token').should('eq', 'new-fake-jwt-token');
  });

  it('shows the server error message when the update fails', () => {
    cy.intercept('PUT', '**/api/users/me', {
      statusCode: 400,
      body: { message: 'Email already used' },
    }).as('updateProfile');

    visitProfile();

    cy.get('input[autocomplete="email"]').clear().type('taken@test.com');
    cy.contains('button', 'Sauvegarder').click();
    cy.wait('@updateProfile');

    cy.get('[role="alert"]').should('contain', 'Email already used');
  });

  it('lists only the subscribed themes', () => {
    visitProfile();

    cy.get('app-theme-card').should('have.length', 1);
    cy.contains('app-theme-card', 'Spring Boot').find('button').should('contain', 'Se désabonner');
  });

  it('unsubscribes from a theme and refreshes the list', () => {
    cy.intercept('DELETE', '**/api/users/me/subscriptions/2', { statusCode: 204 }).as(
      'unsubscribe',
    );

    visitProfile();

    // after the DELETE, the list reload returns an empty array
    cy.intercept('GET', '**/api/users/me/subscriptions', []).as('getSubscriptionsAfter');

    cy.contains('app-theme-card', 'Spring Boot').find('button').click();
    cy.wait('@unsubscribe');
    cy.wait('@getSubscriptionsAfter');

    cy.get('app-theme-card').should('not.exist');
    cy.contains("Vous n'êtes abonné à aucun thème.").should('be.visible');
  });

  it('shows the empty message when the user has no subscription', () => {
    cy.intercept('GET', '**/api/users/me/subscriptions', []).as('getNoSubscriptions');

    cy.visit('/me');
    cy.wait('@getNoSubscriptions');

    cy.contains("Vous n'êtes abonné à aucun thème.").should('be.visible');
  });

  it('shows an error and keeps the list when the unsubscription fails', () => {
  cy.intercept('DELETE', '**/api/users/me/subscriptions/2', {
    statusCode: 404,
    body: { message: 'Theme not found' },
  }).as('unsubscribe');

  visitProfile();

  cy.contains('app-theme-card', 'Spring Boot').find('button').click();
  cy.wait('@unsubscribe');

  cy.get('[role="alert"]').should('contain', 'Une erreur est survenue lors de du désabonnement, réessaie.');
  cy.get('app-theme-card').should('have.length', 1);
});
});
