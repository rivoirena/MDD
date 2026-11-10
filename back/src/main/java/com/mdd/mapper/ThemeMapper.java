package com.mdd.mapper;

import com.mdd.models.Theme;
import com.mdd.payload.response.ThemeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper MapStruct convertissant les entités {@link Theme} en {@link ThemeResponse}.
 * <p>
 * L'implémentation est générée à la compilation et exposée comme bean Spring.
 */
@Mapper(componentModel = "spring")
public interface ThemeMapper {

    /**
     * Convertit un thème en réponse API, enrichie de l'état d'abonnement.
     *
     * @param theme      le thème à convertir
     * @param subscribed {@code true} si l'utilisateur connecté est abonné à ce thème
     * @return la réponse correspondante
     */
    @Mapping(target = "id", source = "theme.id")
    @Mapping(target = "title", source = "theme.title")
    @Mapping(target = "description", source = "theme.description")
    @Mapping(target = "subscribed", source = "subscribed")
    ThemeResponse toResponse(Theme theme, boolean subscribed);
}