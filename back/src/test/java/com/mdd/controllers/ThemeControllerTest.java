package com.mdd.controllers;

import com.mdd.payload.response.ThemeResponse;
import com.mdd.security.JwtAuthenticationFilter;
import com.mdd.security.JwtService;
import com.mdd.security.WebSecurityConfig;
import com.mdd.services.ThemeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ThemeController.class)
@Import({
        WebSecurityConfig.class,
        JwtAuthenticationFilter.class
})
class ThemeControllerTest {

    private static final String EMAIL = "alice@example.com";
    private static final String URL = "/api/themes";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ThemeService themeService;

    // Bean requis par la configuration de sécurité importée
    @MockitoBean
    private JwtService jwtService;

    /** Simule un utilisateur connecté dont le principal est son email (String). */
    private static UsernamePasswordAuthenticationToken authenticatedUser() {
        return new UsernamePasswordAuthenticationToken(EMAIL, null, List.of());
    }

    // ---------- GET /api/themes ----------

    @Test
    void getAllThemes_returnsThemes() throws Exception {
        ThemeResponse javaTheme = new ThemeResponse(
                1L,
                "Java",
                "Actualités et discussions autour de Java",
                true
        );
        ThemeResponse springTheme = new ThemeResponse(
                2L,
                "Spring",
                "Actualités et discussions autour de Spring",
                false
        );

        when(themeService.getAllThemes(EMAIL))
                .thenReturn(List.of(javaTheme, springTheme));

        mockMvc.perform(get(URL)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].description")
                        .value("Actualités et discussions autour de Java"))
                .andExpect(jsonPath("$[0].subscribed").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Spring"))
                .andExpect(jsonPath("$[1].subscribed").value(false));

        verify(themeService).getAllThemes(EMAIL);
    }

    @Test
    void getAllThemes_whenNoThemes_returnsEmptyArray() throws Exception {
        when(themeService.getAllThemes(EMAIL)).thenReturn(List.of());

        mockMvc.perform(get(URL)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(themeService).getAllThemes(EMAIL);
    }

    @Test
    void getAllThemes_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());

        verify(themeService, never()).getAllThemes(any());
    }
}