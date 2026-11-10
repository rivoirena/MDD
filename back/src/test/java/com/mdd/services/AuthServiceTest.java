package com.mdd.services;

import com.mdd.exception.EmailAlreadyExistsException;
import com.mdd.exception.InvalidCredentialsException;
import com.mdd.exception.UsernameAlreadyExistsException;
import com.mdd.models.User;
import com.mdd.payload.request.LoginRequest;
import com.mdd.payload.request.RegisterRequest;
import com.mdd.payload.response.JwtResponse;
import com.mdd.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String EMAIL = "user@test.com";
    private static final String USERNAME = "john";
    private static final String RAW_PASSWORD = "Password1!";
    private static final String ENCODED_PASSWORD = "encoded-password";
    private static final String TOKEN = "jwt-token";

    @Mock private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail(EMAIL);
        registerRequest.setUsername(USERNAME);
        registerRequest.setPassword(RAW_PASSWORD);

        loginRequest = new LoginRequest();
        loginRequest.setIdentifier(EMAIL);
        loginRequest.setPassword(RAW_PASSWORD);

        existingUser = User.builder()
                .id(1L)
                .email(EMAIL)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .build();
    }

    // ---------- register ----------

    @Test
    @DisplayName("register : encode le mot de passe, sauvegarde l'utilisateur et retourne le token")
    void register_shouldEncodePasswordSaveUserAndReturnToken() {
        when(userService.existsByEmail(EMAIL)).thenReturn(false);
        when(userService.existsByUsername(USERNAME)).thenReturn(false);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userService.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(User.class))).thenReturn(TOKEN);

        JwtResponse response = authService.register(registerRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getEmail()).isEqualTo(EMAIL);
        assertThat(saved.getUsername()).isEqualTo(USERNAME);
        assertThat(saved.getPassword())
                .isEqualTo(ENCODED_PASSWORD)
                .isNotEqualTo(RAW_PASSWORD);

        verify(jwtService).generateToken(saved);
        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("register : EmailAlreadyExistsException si l'email est déjà utilisé")
    void register_shouldThrow_whenEmailAlreadyExists() {
        when(userService.existsByEmail(EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userService, never()).save(any());
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("register : UsernameAlreadyExistsException si le nom d'utilisateur est déjà utilisé")
    void register_shouldThrow_whenUsernameAlreadyExists() {
        when(userService.existsByEmail(EMAIL)).thenReturn(false);
        when(userService.existsByUsername(USERNAME)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(UsernameAlreadyExistsException.class);

        verify(userService, never()).save(any());
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    // ---------- login ----------

    @Test
    @DisplayName("login : retourne un token quand les identifiants sont corrects")
    void login_shouldReturnToken_whenCredentialsAreValid() {
        when(userService.findByEmailOrUsername(EMAIL)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateToken(existingUser)).thenReturn(TOKEN);

        JwtResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        verify(jwtService).generateToken(existingUser);
    }

    @Test
    @DisplayName("login : InvalidCredentialsException si l'identifiant est inconnu")
    void login_shouldThrow_whenUserNotFound() {
        when(userService.findByEmailOrUsername(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("login : InvalidCredentialsException si le mot de passe est incorrect")
    void login_shouldThrow_whenPasswordDoesNotMatch() {
        when(userService.findByEmailOrUsername(EMAIL)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);

        verifyNoInteractions(jwtService);
    }
}