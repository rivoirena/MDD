package com.mdd.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corps de la requête de modification du profil ({@code PUT /api/users/me}).
 * <p>
 * Tous les champs sont optionnels : un champ à {@code null} reste inchangé.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    /** Nouvelle adresse email, ou {@code null} pour la conserver. */
    @Email
    @Size(max = 255)
    private String email;

    /** Nouveau nom d'utilisateur, ou {@code null} pour le conserver. */
    @Size(min = 1, max = 50)
    @Pattern(
            regexp = "^[^@\\s]+$",
            message = "Le nom d'utilisateur ne doit pas contenir de @ ni d'espaces"
    )
    private String username;

    /**
     * Nouveau mot de passe en clair (mêmes règles que pour l'inscription),
     * ou {@code null} pour le conserver.
     */
    @Size(min = 8, max = 40)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "Le mot de passe doit contenir une minuscule, une majuscule, un chiffre et un caractère spécial"
    )
    private String password;
}