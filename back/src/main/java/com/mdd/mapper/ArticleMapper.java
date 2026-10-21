package com.mdd.mapper;

import com.mdd.dto.ArticleDto;
import com.mdd.models.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    @Mapping(source = "author.id", target = "author_id")
    @Mapping(source = "theme.id", target = "theme_id")
    ArticleDto toDto(Article article);

    List<ArticleDto> toDto(List<Article> articles);
}