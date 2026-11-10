package com.mdd.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corps de la requête de création d'un article ({@code POST /api/articles}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleRequest {

    /** Identifiant du thème dans lequel publier l'article. */
    @NotNull(message = "Le thème est requis")
    private Long themeId;

    /** Titre de l'article (255 caractères maximum). */
    @NotBlank(message = "Le titre est requis")
    @Size(max = 255, message = "Le titre ne doit pas dépasser 255 caractères")
    private String title;

    /** Contenu de l'article. */
    @NotBlank(message = "Le contenu est requis")
    private String content;
}