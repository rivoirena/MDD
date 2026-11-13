package com.mdd.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représentation d'un thème exposée par l'API, enrichie de l'état d'abonnement
 * de l'utilisateur connecté.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeResponse {
    /** Identifiant du thème. */
    private Long id;
    /** Titre du thème. */
    private String title;
    /** Description du thème. */
    private String description;
    /** {@code true} si l'utilisateur connecté est abonné à ce thème. */
    private boolean subscribed;
}