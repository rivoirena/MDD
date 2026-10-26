package com.mdd.mapper;

import com.mdd.models.Theme;
import com.mdd.payload.response.ThemeResponse;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ThemeMapper {

    List<ThemeResponse> toResponseList(Set<Theme> themes);
}