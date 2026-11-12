/// <reference types="cypress" />
// ***********************************************
// This example commands.ts shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
//
// declare global {
//   namespace Cypress {
//     interface Chainable {
//       login(email: string, password: string): Chainable<void>
//       drag(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
//       dismiss(subject: string, options?: Partial<TypeOptions>): Chainable<Element>
//       visit(originalFn: CommandOriginalFn, url: string, options: Partial<VisitOptions>): Chainable<Element>
//     }
//   }
// }

declare global {
  namespace Cypress {
    interface Chainable {
      loginByToken(): Chainable<void>;
      typeInField(selector: string, text: string): Chainable<void>;
      selectMatOption(selector: string, optionText: string): Chainable<void>;
    }
  }
}

Cypress.Commands.add('loginByToken', () => {
  window.localStorage.setItem('token', 'fake-jwt-token');
});

Cypress.Commands.add('typeInField', (selector: string, text: string) => {
  cy.get(selector).type(text, { force: true });
});

Cypress.Commands.add('selectMatOption', (selector: string, optionText: string) => {
  cy.get(selector).focus().type('{enter}');
  cy.get('mat-option[role="option"]').should('have.length.greaterThan', 0);
  cy.contains('mat-option[role="option"]', optionText).click();
});

export {};