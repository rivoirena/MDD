package com.mdd.exception;

/**
 * Exception levée lors de l'inscription ou de la modification du profil
 * lorsque l'adresse email est déjà utilisée par un autre compte.
 * <p>
 * Traduite en réponse HTTP {@code 409 Conflict} par {@link GlobalExceptionHandler}.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /** Construit l'exception avec le message par défaut « Email already exists ». */
    public EmailAlreadyExistsException() {
        super("Email already exists");
    }
}