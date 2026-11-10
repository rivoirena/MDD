package com.mdd.mapper;

import com.mdd.models.Theme;
import com.mdd.payload.response.ThemeResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class ThemeMapperTest {

    private final ThemeMapper mapper = Mappers.getMapper(ThemeMapper.class);

    @Test
    void toResponse_mapsThemeAndSubscriptionStatus() {
        Theme theme = Theme.builder()
                .id(1L)
                .title("Java")
                .description("Programmation Java")
                .build();

        ThemeResponse result = mapper.toResponse(theme, true);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java", result.getTitle());
        assertEquals("Programmation Java", result.getDescription());
        assertTrue(result.isSubscribed());
    }

    @Test
    void toResponse_mapsUnsubscribedStatus() {
        Theme theme = Theme.builder()
                .id(1L)
                .title("Java")
                .description("Programmation Java")
                .build();

        ThemeResponse result = mapper.toResponse(theme, false);

        assertNotNull(result);
        assertFalse(result.isSubscribed());
    }

    @Test
    void toResponse_returnsNullWhenThemeIsNull() {
        assertNull(mapper.toResponse(null, true));
    }
}