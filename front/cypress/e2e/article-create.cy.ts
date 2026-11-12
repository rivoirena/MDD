describe('Article creation', () => {
  beforeEach(() => {
    cy.loginByToken();
    cy.intercept('GET', '**/api/themes', { fixture: 'themes.json' }).as('getThemes');
    cy.intercept('GET', '**/api/articles*', { fixture: 'articles.json' }).as('getArticles');
  });

  const visitCreate = () => {
    cy.visit('/articles/new');
    cy.wait('@getThemes');
  };

  it('disables the submit button when the form is empty', () => {
    visitCreate();
    cy.contains('button', 'Créer').should('be.disabled');
  });

  it('shows validation errors on empty fields', () => {
    visitCreate();

    cy.get('input[matinput]').focus().blur();
    cy.contains('Titre requis').should('be.visible');

    cy.get('textarea').focus().blur();
    cy.contains('Contenu requis').should('be.visible');
  });

  it('creates an article and redirects to /articles', () => {
    cy.intercept('POST', '**/api/articles', {
      statusCode: 201,
      body: {
        id: 3,
        title: 'Mon nouvel article',
        content: 'Contenu de test',
        createdAt: '2026-11-11T12:00:00',
        authorId: 1,
        authorName: 'alice',
        themeId: 1,
        themeName: 'Angular',
      },
    }).as('createArticle');

    visitCreate();

    cy.selectMatOption('mat-select', 'Angular');
    cy.typeInField('input[matinput]', 'Mon nouvel article');
    cy.typeInField('textarea', 'Contenu de test');

    cy.contains('button', 'Créer').should('be.enabled').click();

    cy.wait('@createArticle').its('request.body').should('deep.equal', {
      themeId: 1,
      title: 'Mon nouvel article',
      content: 'Contenu de test',
    });

    cy.url().should('include', '/articles');
    cy.url().should('not.include', '/new');
  });

  it('shows the server error message when creation fails', () => {
    cy.intercept('POST', '**/api/articles', {
      statusCode: 400,
      body: { message: 'Thème introuvable' },
    }).as('createArticle');

    visitCreate();

    cy.selectMatOption('mat-select', 'Angular');
    cy.get('input[matinput]').type('Titre');
    cy.get('textarea').type('Contenu');
    cy.contains('button', 'Créer').click();

    cy.wait('@createArticle');
    cy.get('[role="alert"]').should('contain', 'Thème introuvable');
    cy.url().should('include', '/articles/new');
  });

  it('goes back to the list with the back button', () => {
    visitCreate();
    cy.get('button[aria-label="Retour"]').click();
    cy.url().should('match', /\/articles$/);
  });
});
