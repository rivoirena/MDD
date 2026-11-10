package com.mdd.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représentation du profil de l'utilisateur connecté ({@code GET /api/users/me}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    /** Adresse email de l'utilisateur. */
    private String email;
    /** Nom d'utilisateur. */
    private String username;
}