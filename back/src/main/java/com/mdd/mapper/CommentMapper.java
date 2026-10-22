package com.mdd.mapper;

import com.mdd.dto.CommentDto;
import com.mdd.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "article.id", target = "articleId")
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comments);
}