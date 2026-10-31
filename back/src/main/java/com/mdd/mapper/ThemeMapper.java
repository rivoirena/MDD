package com.mdd.mapper;

import com.mdd.models.Theme;
import com.mdd.payload.response.ThemeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ThemeMapper {

    @Mapping(target = "id", source = "theme.id")
    @Mapping(target = "title", source = "theme.title")
    @Mapping(target = "description", source = "theme.description")
    @Mapping(target = "subscribed", source = "subscribed")
    ThemeResponse toResponse(Theme theme, boolean subscribed);
}