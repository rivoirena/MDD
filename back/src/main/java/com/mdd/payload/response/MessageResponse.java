package com.mdd.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Réponse générique portant un message, utilisée notamment pour les erreurs
 * renvoyées par {@link com.mdd.exception.GlobalExceptionHandler}.
 */
@Data
@AllArgsConstructor
public class MessageResponse {
    /** Message destiné au client. */
    private String message;
}
