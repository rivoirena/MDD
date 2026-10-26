package com.mdd.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    // null = inchangé
    @Email
    @Size(max = 255)
    private String email;

    // null = inchangé
    @Size(min = 1, max = 50)
    @Pattern(
            regexp = "^[^@\\s]+$",
            message = "Le nom d'utilisateur ne doit pas contenir de @ ni d'espaces"
    )
    private String username;

    // null = inchangé
    @Size(min = 8, max = 40)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "Le mot de passe doit contenir une minuscule, une majuscule, un chiffre et un caractère spécial"
    )
    private String password;
}