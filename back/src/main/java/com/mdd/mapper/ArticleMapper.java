package com.mdd.mapper;

import com.mdd.dto.ArticleDto;
import com.mdd.models.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper MapStruct convertissant les entités {@link Article} en {@link ArticleDto}.
 * <p>
 * L'implémentation est générée à la compilation et exposée comme bean Spring.
 */
@Mapper(componentModel = "spring")
public interface ArticleMapper {

    /**
     * Convertit un article en DTO.
     * <p>
     * Les associations sont aplaties : l'auteur est exposé par son identifiant et son nom
     * d'utilisateur, le thème par son identifiant et son titre.
     *
     * @param article l'article à convertir
     * @return le DTO correspondant
     */
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorName")
    @Mapping(source = "theme.id", target = "themeId")
    @Mapping(source = "theme.title", target = "themeName")
    ArticleDto toDto(Article article);

    /**
     * Convertit une liste d'articles en liste de DTO.
     *
     * @param articles les articles à convertir
     * @return la liste des DTO correspondants, dans le même ordre
     */
    List<ArticleDto> toDto(List<Article> articles);
}