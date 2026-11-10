package com.mdd.controllers;

import com.mdd.exception.GlobalExceptionHandler;
import com.mdd.payload.response.ThemeResponse;
import com.mdd.security.JwtAuthenticationFilter;
import com.mdd.security.JwtService;
import com.mdd.security.WebSecurityConfig;
import com.mdd.services.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SubscriptionController.class)
@Import({
        WebSecurityConfig.class,
        JwtAuthenticationFilter.class,
        GlobalExceptionHandler.class
})
class SubscriptionControllerTest {

    private static final String EMAIL = "alice@example.com";
    private static final Long THEME_ID = 1L;
    private static final String URL = "/api/users/me/subscriptions";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SubscriptionService subscriptionService;

    // Beans requis par la configuration de sécurité importée
    @MockitoBean
    private JwtService jwtService;

    /** Simule un utilisateur connecté dont le principal est son email (String). */
    private static UsernamePasswordAuthenticationToken authenticatedUser() {
        return new UsernamePasswordAuthenticationToken(EMAIL, null, List.of());
    }

    // ---------- GET /api/users/me/subscriptions ----------

    @Test
    void getMySubscriptions_returnsSubscriptions() throws Exception {
        ThemeResponse response = new ThemeResponse(
                THEME_ID,
                "Java",
                "Actualités et discussions autour de Java",
                true
        );

        when(subscriptionService.getSubscriptions(EMAIL))
                .thenReturn(List.of(response));

        mockMvc.perform(get(URL)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(THEME_ID))
                .andExpect(jsonPath("$[0].title").value("Java"))
                .andExpect(jsonPath("$[0].description")
                        .value("Actualités et discussions autour de Java"))
                .andExpect(jsonPath("$[0].subscribed").value(true));

        verify(subscriptionService).getSubscriptions(EMAIL);
    }

    @Test
    void getMySubscriptions_whenNoSubscriptions_returnsEmptyArray() throws Exception {
        when(subscriptionService.getSubscriptions(EMAIL)).thenReturn(List.of());

        mockMvc.perform(get(URL)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(subscriptionService).getSubscriptions(EMAIL);
    }

    @Test
    void getMySubscriptions_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());

        verify(subscriptionService, never()).getSubscriptions(any());
    }

    // ---------- POST /api/users/me/subscriptions/{themeId} ----------

    @Test
    void subscribe_returns204() throws Exception {
        mockMvc.perform(post(URL + "/" + THEME_ID)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isNoContent());

        verify(subscriptionService).subscribe(EMAIL, THEME_ID);
    }

    @Test
    void subscribe_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(post(URL + "/" + THEME_ID))
                .andExpect(status().isUnauthorized());

        verify(subscriptionService, never()).subscribe(any(), any());
    }

    @Test
    void subscribe_whenThemeDoesNotExist_returns404() throws Exception {
        doThrow(new EntityNotFoundException("Thème introuvable"))
                .when(subscriptionService).subscribe(EMAIL, THEME_ID);

        mockMvc.perform(post(URL + "/" + THEME_ID)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Thème introuvable"));

        verify(subscriptionService).subscribe(EMAIL, THEME_ID);
    }

    // ---------- DELETE /api/users/me/subscriptions/{themeId} ----------

    @Test
    void unsubscribe_returns204() throws Exception {
        mockMvc.perform(delete(URL + "/" + THEME_ID)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isNoContent());

        verify(subscriptionService).unsubscribe(EMAIL, THEME_ID);
    }

    @Test
    void unsubscribe_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(delete(URL + "/" + THEME_ID))
                .andExpect(status().isUnauthorized());

        verify(subscriptionService, never()).unsubscribe(any(), any());
    }

    @Test
    void unsubscribe_whenThemeDoesNotExist_returns404() throws Exception {
        doThrow(new EntityNotFoundException("Thème introuvable"))
                .when(subscriptionService).unsubscribe(EMAIL, THEME_ID);

        mockMvc.perform(delete(URL + "/" + THEME_ID)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Thème introuvable"));

        verify(subscriptionService).unsubscribe(EMAIL, THEME_ID);
    }
}