package com.mdd.controllers;

import com.mdd.exception.GlobalExceptionHandler;
import com.mdd.payload.request.UpdateProfileRequest;
import com.mdd.payload.response.JwtResponse;
import com.mdd.payload.response.UserResponse;
import com.mdd.security.JwtAuthenticationFilter;
import com.mdd.security.JwtService;
import com.mdd.security.WebSecurityConfig;
import com.mdd.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({
        WebSecurityConfig.class,
        JwtAuthenticationFilter.class,
        GlobalExceptionHandler.class
})
class UserControllerTest {

    private static final String EMAIL = "alice@example.com";
    private static final String URL = "/api/users/me";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    // Beans requis par la configuration de sécurité importée
    @MockitoBean
    private JwtService jwtService;

    /** Simule un utilisateur connecté dont le principal est son email (String). */
    private static UsernamePasswordAuthenticationToken authenticatedUser() {
        return new UsernamePasswordAuthenticationToken(EMAIL, null, List.of());
    }

    // ---------- GET /api/users/me ----------

    @Test
    void getProfile_returnsUserProfile() throws Exception {
        when(userService.getProfile(EMAIL))
                .thenReturn(new UserResponse(EMAIL, "alice"));

        mockMvc.perform(get(URL)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.username").value("alice"));

        verify(userService).getProfile(EMAIL);
    }

    @Test
    void getProfile_whenUserDoesNotExist_returns404() throws Exception {
        when(userService.getProfile(EMAIL))
                .thenThrow(new EntityNotFoundException("Utilisateur introuvable"));

        mockMvc.perform(get(URL)
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Utilisateur introuvable"));
    }

    @Test
    void getProfile_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).getProfile(any());
    }

    // ---------- PUT /api/users/me ----------

    @Test
    void updateProfile_withValidRequest_returnsNewJwt() throws Exception {
        UpdateProfileRequest request =
                new UpdateProfileRequest("new-alice@example.com", "newAlice", null);

        when(userService.updateProfile(eq(EMAIL), any(UpdateProfileRequest.class)))
                .thenReturn(new JwtResponse("new.jwt.token"));

        mockMvc.perform(put(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new.jwt.token"));

        verify(userService).updateProfile(eq(EMAIL), any(UpdateProfileRequest.class));
    }

    @Test
    void updateProfile_withInvalidEmail_returns400() throws Exception {
        UpdateProfileRequest request =
                new UpdateProfileRequest("adresse-invalide", null, null);

        mockMvc.perform(put(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_withUsernameContainingSpaces_returns400() throws Exception {
        UpdateProfileRequest request =
                new UpdateProfileRequest(null, "alice smith", null);

        mockMvc.perform(put(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_withMalformedJson_returns400() throws Exception {
        mockMvc.perform(put(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ pas du json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Malformed or missing request body"));

        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_withoutAuthentication_returns401() throws Exception {
        UpdateProfileRequest request =
                new UpdateProfileRequest(null, "newAlice", null);

        mockMvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_whenUserDoesNotExist_returns404() throws Exception {
        UpdateProfileRequest request =
                new UpdateProfileRequest(null, "newAlice", null);

        when(userService.updateProfile(eq(EMAIL), any(UpdateProfileRequest.class)))
                .thenThrow(new EntityNotFoundException("Utilisateur introuvable"));

        mockMvc.perform(put(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Utilisateur introuvable"));
    }
}