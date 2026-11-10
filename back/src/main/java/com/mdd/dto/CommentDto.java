package com.mdd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Représentation d'un commentaire exposée par l'API.
 * <p>
 * Aplatit les associations de l'entité {@link com.mdd.models.Comment} :
 * l'auteur est exposé par son identifiant et son nom, l'article par son identifiant.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    /** Identifiant du commentaire. */
    private Long id;

    /** Contenu du commentaire. */
    private String content;

    /** Identifiant de l'auteur. */
    private Long authorId;

    /** Nom d'utilisateur de l'auteur. */
    private String authorName;

    /** Identifiant de l'article commenté. */
    private Long articleId;

    /** Date de création du commentaire. */
    private LocalDateTime createdAt;
}