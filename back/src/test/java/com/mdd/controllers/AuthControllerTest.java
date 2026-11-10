package com.mdd.controllers;

import tools.jackson.databind.json.JsonMapper;
import com.mdd.exception.EmailAlreadyExistsException;
import com.mdd.exception.GlobalExceptionHandler;
import com.mdd.exception.InvalidCredentialsException;
import com.mdd.exception.UsernameAlreadyExistsException;
import com.mdd.payload.request.LoginRequest;
import com.mdd.payload.request.RegisterRequest;
import com.mdd.payload.response.JwtResponse;
import com.mdd.security.JwtAuthenticationFilter;
import com.mdd.security.JwtService;
import com.mdd.security.WebSecurityConfig;
import com.mdd.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({WebSecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AuthService authService;

    // Requis par JwtAuthenticationFilter (importé avec la config de sécurité)
    @MockitoBean
    private JwtService jwtService;

    private RegisterRequest validRegister() {
        return new RegisterRequest("john@example.com", "john", "Password1!");
    }

    private LoginRequest validLogin() {
        LoginRequest request = new LoginRequest();
        request.setIdentifier("john@example.com");
        request.setPassword("Password1!");
        return request;
    }

    // ---------- REGISTER ----------

    @Test
    void register_shouldReturn201AndToken_whenRequestIsValid() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn(new JwtResponse("jwt-token"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validRegister())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void register_shouldReturn400_whenEmailIsInvalid() throws Exception {
        RegisterRequest request = validRegister();
        request.setEmail("not-an-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("email")));

        verify(authService, never()).register(any());
    }

    @Test
    void register_shouldReturn400_whenUsernameContainsAtSign() throws Exception {
        RegisterRequest request = validRegister();
        request.setUsername("john@doe");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("username")));

        verify(authService, never()).register(any());
    }

    @Test
    void register_shouldReturn400_whenUsernameContainsSpace() throws Exception {
        RegisterRequest request = validRegister();
        request.setUsername("john doe");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_shouldReturn400_whenPasswordIsTooWeak() throws Exception {
        RegisterRequest request = validRegister();
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("password")));

        verify(authService, never()).register(any());
    }

    @Test
    void register_shouldReturn400_whenPasswordIsTooShort() throws Exception {
        RegisterRequest request = validRegister();
        request.setPassword("Aa1!");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_shouldReturn400_whenFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any());
    }

    @Test
    void register_shouldReturn400_whenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed or missing request body"));
    }

    @Test
    void register_shouldReturn400_whenBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed or missing request body"));
    }

    @Test
    void register_shouldReturn409_whenEmailAlreadyExists() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenThrow(new EmailAlreadyExistsException());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validRegister())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void register_shouldReturn409_whenUsernameAlreadyExists() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenThrow(new UsernameAlreadyExistsException());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validRegister())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already exists"));
    }

    // ---------- LOGIN ----------

    @Test
    void login_shouldReturn200AndToken_whenCredentialsAreValid() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(new JwtResponse("jwt-token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validLogin())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_shouldReturn400_whenIdentifierIsBlank() throws Exception {
        LoginRequest request = validLogin();
        request.setIdentifier("  ");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("identifier")));

        verify(authService, never()).login(any());
    }

    @Test
    void login_shouldReturn400_whenPasswordIsMissing() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"identifier\":\"john@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("password")));

        verify(authService, never()).login(any());
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validLogin())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Identifiants invalides"));
    }

    // ---------- SÉCURITÉ ----------

    @Test
    void authRoutes_shouldBePublic_withoutAuthorizationHeader() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(new JwtResponse("jwt-token"));

        // Pas de header Authorization : la route doit être accessible (pas de 401/403)
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validLogin())))
                .andExpect(status().isOk());
    }
}