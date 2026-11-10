package com.mdd.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corps de la requête d'inscription ({@code POST /api/auth/register}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** Adresse email, unique (255 caractères maximum). */
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    /** Nom d'utilisateur, unique (50 caractères maximum), sans {@code @} ni espaces. */
    @NotBlank
    @Size(max = 50)
    @Pattern(
            regexp = "^[^@\\s]+$",
            message = "Le nom d'utilisateur ne doit pas contenir de @ ni d'espaces"
    )
    private String username;

    /**
     * Mot de passe en clair (8 à 40 caractères) contenant au moins une minuscule,
     * une majuscule, un chiffre et un caractère spécial.
     */
    @NotBlank
    @Size(min = 8, max = 40)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "Le mot de passe doit contenir une minuscule, une majuscule, un chiffre et un caractère spécial"
    )
    private String password;
}