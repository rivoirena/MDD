package com.mdd.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Corps de la requête d'ajout d'un commentaire
 * ({@code POST /api/articles/{articleId}/comments}).
 */
@Data
public class CreateCommentRequest {

    /** Contenu du commentaire. */
    @NotBlank(message = "Le contenu est obligatoire")
    private String content;
}