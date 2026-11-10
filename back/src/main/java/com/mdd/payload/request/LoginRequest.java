package com.mdd.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Corps de la requête de connexion ({@code POST /api/auth/login}).
 */
@Data
public class LoginRequest {

    /** Email ou nom d'utilisateur de la personne qui se connecte. */
    @NotBlank(message = "L'email ou le nom d'utilisateur est obligatoire")
    private String identifier;

    /** Mot de passe en clair. */
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}