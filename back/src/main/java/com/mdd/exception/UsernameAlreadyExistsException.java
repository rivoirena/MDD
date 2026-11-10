package com.mdd.exception;

/**
 * Exception levée lors de l'inscription ou de la modification du profil
 * lorsque le nom d'utilisateur est déjà utilisé par un autre compte.
 * <p>
 * Traduite en réponse HTTP {@code 409 Conflict} par {@link GlobalExceptionHandler}.
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    /** Construit l'exception avec le message par défaut « Username already exists ». */
    public UsernameAlreadyExistsException() {
        super("Username already exists");
    }
}
