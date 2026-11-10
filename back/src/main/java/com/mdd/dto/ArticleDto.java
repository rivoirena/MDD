package com.mdd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Représentation d'un article exposée par l'API.
 * <p>
 * Aplatit les associations de l'entité {@link com.mdd.models.Article} :
 * l'auteur et le thème sont exposés par leur identifiant et leur nom.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {
    /** Identifiant de l'article. */
    private Long id;

    /** Titre de l'article. */
    private String title;

    /** Contenu de l'article. */
    private String content;

    /** Identifiant de l'auteur. */
    private Long authorId;

    /** Nom d'utilisateur de l'auteur. */
    private String authorName;

    /** Identifiant du thème. */
    private Long themeId;

    /** Titre du thème. */
    private String themeName;

    /** Date de création de l'article. */
    private LocalDateTime createdAt;
}
