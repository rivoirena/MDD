package com.mdd.exception;

/**
 * Exception levée lors de la connexion lorsque l'email ou le mot de passe est incorrect.
 * <p>
 * Traduite en réponse HTTP {@code 401 Unauthorized} par {@link GlobalExceptionHandler}.
 */
public class InvalidCredentialsException extends RuntimeException {

    /** Construit l'exception avec le message par défaut « Identifiants invalides ». */
    public InvalidCredentialsException() {
        super("Identifiants invalides");
    }
}