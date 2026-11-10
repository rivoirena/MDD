package com.mdd.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse renvoyée après une inscription, une connexion ou une modification du profil.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    /** Token JWT à envoyer dans le header {@code Authorization: Bearer <token>}. */
    private String token;
}
