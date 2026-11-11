describe('Article detail', () => {
  beforeEach(() => {
    cy.loginByToken();

    cy.fixture('articles.json').then((articles) => {
      cy.intercept('GET', '**/api/articles/1', articles[0]).as('getArticle');
    });
    cy.intercept('GET', '**/api/articles/1/comments', { fixture: 'comments.json' }).as(
      'getComments',
    );
  });

  const visitDetail = () => {
    cy.visit('/articles/1');
    cy.wait(['@getArticle', '@getComments']);
  };

  it('displays the article and its comments', () => {
    visitDetail();

    cy.contains('h1', 'Découvrir les signals Angular').should('be.visible');
    cy.contains('alice').should('be.visible');
    cy.contains('Angular').should('be.visible');
    cy.contains('Les signals simplifient la gestion de la réactivité').should('be.visible');

    cy.get('.comments li').should('have.length', 2);
    cy.contains('.comments li', 'bob').should('contain', 'Super article');
    cy.contains('.comments li', 'carol').should('contain', 'Très clair');
  });

  it('shows an empty message when there is no comment', () => {
    cy.intercept('GET', '**/api/articles/1/comments', []).as('getComments');
    visitDetail();

    cy.contains('Aucun commentaire pour le moment.').should('be.visible');
  });

  it('disables the send button when the comment is empty', () => {
    visitDetail();
    cy.get('button[aria-label="Envoyer le commentaire"]').should('be.disabled');
  });

  it('posts a comment and reloads the list', () => {
    cy.intercept('POST', '**/api/articles/1/comments', {
      statusCode: 201,
      body: {
        id: 3,
        content: 'Mon commentaire',
        authorId: 1,
        authorName: 'alice',
        articleId: 1,
        createdAt: '2026-11-11T12:00:00',
      },
    }).as('postComment');

    visitDetail();

    // Après l'envoi, la liste est rechargée : on renvoie 3 commentaires
    cy.fixture('comments.json').then((comments) => {
      cy.intercept('GET', '**/api/articles/1/comments', [
        ...comments,
        {
          id: 3,
          content: 'Mon commentaire',
          authorId: 1,
          authorName: 'alice',
          articleId: 1,
          createdAt: '2026-11-11T12:00:00',
        },
      ]).as('getCommentsReloaded');
    });

    cy.get('textarea').type('Mon commentaire');
    cy.get('button[aria-label="Envoyer le commentaire"]').should('be.enabled').click();

    cy.wait('@postComment').its('request.body').should('deep.equal', {
      content: 'Mon commentaire',
    });
    cy.wait('@getCommentsReloaded');

    cy.get('.comments li').should('have.length', 3);
    cy.contains('.comments li', 'Mon commentaire').should('be.visible');
    cy.get('textarea').should('have.value', '');
  });

  it('goes back to the list with the back button', () => {
    visitDetail();
    cy.get('button[aria-label="Retour"]').click();
    cy.url().should('match', /\/articles$/);
  });
});