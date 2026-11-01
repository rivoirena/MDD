package com.mdd.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {

    @NotBlank(message = "Le contenu est obligatoire")
    private String content;
}