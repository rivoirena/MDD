package com.mdd.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "L'email ou le nom d'utilisateur est obligatoire")
    private String identifier;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;
}