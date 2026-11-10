package com.mdd.exception;

import com.mdd.payload.response.MessageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestionnaire global des exceptions de l'API.
 * <p>
 * Intercepte les exceptions levées par les contrôleurs et les services, et les convertit
 * en réponses HTTP au format JSON ({@code { "message": "..." }}) avec le code de statut adapté.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère les conflits d'unicité (email ou nom d'utilisateur déjà utilisé).
     *
     * @param ex l'exception levée
     * @return une réponse {@code 409 Conflict} contenant le message de l'exception
     */
    @ExceptionHandler({EmailAlreadyExistsException.class, UsernameAlreadyExistsException.class})
    public ResponseEntity<MessageResponse> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new MessageResponse(ex.getMessage()));
    }

    /**
     * Gère les erreurs de validation du corps de la requête ({@code @Valid}).
     * Les messages d'erreur de chaque champ invalide sont concaténés sous la forme
     * {@code champ : message, champ : message}.
     *
     * @param ex l'exception levée par la validation
     * @return une réponse {@code 400 Bad Request} listant les champs invalides
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new MessageResponse(message));
    }

    /**
     * Gère les identifiants de connexion incorrects.
     *
     * @param ex l'exception levée
     * @return une réponse {@code 401 Unauthorized} contenant le message de l'exception
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<MessageResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MessageResponse(ex.getMessage()));
    }

    /**
     * Gère les ressources introuvables (utilisateur, thème, article...).
     *
     * @param ex l'exception levée
     * @return une réponse {@code 404 Not Found} contenant le message de l'exception
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<MessageResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(ex.getMessage()));
    }
    /**
     * Gère les requêtes dont le corps JSON est malformé ou absent.
     *
     * @param ex l'exception levée
     * @return une réponse {@code 400 Bad Request} avec le message générique
     *         {@code { "error": "Malformed or missing request body" }}
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Malformed or missing request body"));
    }
}
