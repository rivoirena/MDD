package com.mdd.mapper;

import com.mdd.dto.ArticleDto;
import com.mdd.models.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorName")
    @Mapping(source = "theme.id", target = "themeId")
    @Mapping(source = "theme.title", target = "themeName")
    ArticleDto toDto(Article article);

    List<ArticleDto> toDto(List<Article> articles);
}