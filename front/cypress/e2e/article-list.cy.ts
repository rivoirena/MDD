describe('Article list', () => {
  beforeEach(() => {
    cy.loginByToken();
    cy.intercept('GET', '**/api/articles*', { fixture: 'articles.json' }).as('getArticles');
  });

  it('displays the articles from the API', () => {
    cy.visit('/articles');
    cy.wait('@getArticles');

    cy.get('app-article-card').should('have.length', 2);
    cy.contains('app-article-card', 'Découvrir les signals Angular').within(() => {
      cy.contains('alice');
      cy.contains('10/11/2026');
    });
    cy.contains('app-article-card', 'Sécuriser une API avec JWT').within(() => {
      cy.contains('bob');
      cy.contains('09/11/2026');
    });
  });

  it('requests articles in descending order by default', () => {
    cy.visit('/articles');
    cy.wait('@getArticles').its('request.url').should('include', 'sort=desc');
  });

  it('requests articles in ascending order when "Plus anciens" is selected', () => {
    cy.visit('/articles');
    cy.wait('@getArticles');

    cy.contains('button', 'Trier par').click();
    cy.contains('button', 'Plus anciens').click();

    cy.wait('@getArticles').its('request.url').should('include', 'sort=asc');
  });

  it('requests articles in descending order again when "Plus récents" is selected', () => {
    cy.visit('/articles');
    cy.wait('@getArticles');

    cy.contains('button', 'Trier par').click();
    cy.contains('button', 'Plus anciens').click();
    cy.wait('@getArticles');

    cy.contains('button', 'Trier par').click();
    cy.contains('button', 'Plus récents').click();

    cy.wait('@getArticles').its('request.url').should('include', 'sort=desc');
  });

  it('renders articles in the order returned by the API', () => {
    cy.intercept('GET', '**/api/articles*', (req) => {
      req.reply({ fixture: 'articles.json' });
    }).as('getSorted');

    cy.visit('/articles');
    cy.wait('@getSorted');

    cy.get('app-article-card').first().should('contain', 'Découvrir les signals Angular');
    cy.get('app-article-card').last().should('contain', 'Sécuriser une API avec JWT');
  });

  it('shows the empty message when the API returns no article', () => {
    cy.intercept('GET', '**/api/articles*', []).as('getEmpty');

    cy.visit('/articles');
    cy.wait('@getEmpty');

    cy.contains('Abonnez-vous à des thèmes pour voir des articles ici.').should('be.visible');
    cy.get('app-article-card').should('not.exist');
  });

  it('navigates to the article detail when a card is clicked', () => {
    cy.intercept('GET', '**/api/articles/1', {
      id: 1,
      title: 'Découvrir les signals Angular',
      content: 'Les signals simplifient la gestion de la réactivité dans Angular.',
      createdAt: '2026-11-10T09:30:00',
      authorId: 1,
      authorName: 'alice',
      themeId: 1,
      themeName: 'Angular',
    }).as('getArticle');
    cy.intercept('GET', '**/api/articles/1/comments', []).as('getComments');

    cy.visit('/articles');
    cy.wait('@getArticles');

    cy.contains('app-article-card', 'Découvrir les signals Angular').click();

    cy.url().should('include', '/articles/1');
  });

  it('navigates to the article creation page', () => {
    cy.fixture('themes.json').then((themes) => {
      cy.intercept('GET', '**/api/themes*', themes).as('getThemes');
    });

    cy.visit('/articles');
    cy.wait('@getArticles');

    cy.contains('a', 'Créer un article').click();

    cy.wait('@getThemes');

    cy.url().should('include', '/articles/new');
  });

  it('logs out and redirects to the home page', () => {
    cy.visit('/articles');
    cy.wait('@getArticles');

    cy.get('nav.desktop').contains('button', 'Se déconnecter').click();

    cy.url().should('eq', Cypress.config('baseUrl') + '/');
    cy.window().its('localStorage').invoke('getItem', 'token').should('be.null');
    cy.contains('a', 'Se connecter').should('be.visible');
  });
});
