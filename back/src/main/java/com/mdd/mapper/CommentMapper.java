package com.mdd.mapper;

import com.mdd.dto.CommentDto;
import com.mdd.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct convertissant les entités {@link Comment} en {@link CommentDto}.
 * <p>
 * L'implémentation est générée à la compilation et exposée comme bean Spring.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

    /**
     * Convertit un commentaire en DTO.
     * <p>
     * L'auteur est exposé par son identifiant et son nom d'utilisateur,
     * l'article par son identifiant.
     *
     * @param comment le commentaire à convertir
     * @return le DTO correspondant
     */
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorName")
    @Mapping(source = "article.id", target = "articleId")
    CommentDto toDto(Comment comment);

    /**
     * Convertit une liste de commentaires en liste de DTO.
     *
     * @param comments les commentaires à convertir
     * @return la liste des DTO correspondants, dans le même ordre
     */
    List<CommentDto> toDto(List<Comment> comments);
}