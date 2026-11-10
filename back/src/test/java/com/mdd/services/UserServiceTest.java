package com.mdd.services;

import com.mdd.exception.EmailAlreadyExistsException;
import com.mdd.exception.UsernameAlreadyExistsException;
import com.mdd.mapper.ThemeMapper;
import com.mdd.models.User;
import com.mdd.payload.request.UpdateProfileRequest;
import com.mdd.payload.response.JwtResponse;
import com.mdd.payload.response.UserResponse;
import com.mdd.repository.UserRepository;
import com.mdd.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
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
class UserServiceTest {

    private static final String EMAIL = "user@test.com";
    private static final String USERNAME = "john";
    private static final String ENCODED_PASSWORD = "encoded-old";
    private static final String TOKEN = "jwt-token";

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private ThemeMapper themeMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UpdateProfileRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email(EMAIL)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .build();
        request = new UpdateProfileRequest();
    }

    // ---------- Délégations simples ----------

    @Test
    @DisplayName("save : délègue au repository et retourne l'utilisateur sauvegardé")
    void save_shouldDelegateToRepository() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.save(user);

        assertThat(result).isSameAs(user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("findByEmail : retourne l'utilisateur trouvé")
    void findByEmail_shouldReturnUser() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThat(userService.findByEmail(EMAIL)).contains(user);
    }

    @Test
    @DisplayName("findById : retourne l'utilisateur trouvé")
    void findById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(userService.findById(1L)).contains(user);
    }

    @Test
    @DisplayName("existsByEmail : délègue au repository")
    void existsByEmail_shouldDelegate() {
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        assertThat(userService.existsByEmail(EMAIL)).isTrue();
    }

    @Test
    @DisplayName("existsByUsername : délègue au repository")
    void existsByUsername_shouldDelegate() {
        when(userRepository.existsByUsername(USERNAME)).thenReturn(false);

        assertThat(userService.existsByUsername(USERNAME)).isFalse();
    }

    // ---------- findByEmailOrUsername ----------

    @Test
    @DisplayName("findByEmailOrUsername : passe le même identifiant pour l'email et le username (cas email)")
    void findByEmailOrUsername_shouldQueryBothFields_withEmail() {
        when(userRepository.findByEmailOrUsername(EMAIL, EMAIL)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailOrUsername(EMAIL);

        assertThat(result).contains(user);
        verify(userRepository).findByEmailOrUsername(EMAIL, EMAIL);
    }

    @Test
    @DisplayName("findByEmailOrUsername : passe le même identifiant pour l'email et le username (cas username)")
    void findByEmailOrUsername_shouldQueryBothFields_withUsername() {
        when(userRepository.findByEmailOrUsername(USERNAME, USERNAME)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmailOrUsername(USERNAME);

        assertThat(result).contains(user);
        verify(userRepository).findByEmailOrUsername(USERNAME, USERNAME);
    }

    @Test
    @DisplayName("findByEmailOrUsername : retourne un Optional vide si aucun utilisateur ne correspond")
    void findByEmailOrUsername_shouldReturnEmpty_whenNotFound() {
        when(userRepository.findByEmailOrUsername("inconnu", "inconnu")).thenReturn(Optional.empty());

        assertThat(userService.findByEmailOrUsername("inconnu")).isEmpty();
    }

    // ---------- getProfile ----------

    @Test
    @DisplayName("getProfile : retourne l'email et le username de l'utilisateur")
    void getProfile_shouldReturnProfile() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        UserResponse response = userService.getProfile(EMAIL);

        assertThat(response.getEmail()).isEqualTo(EMAIL);
        assertThat(response.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    @DisplayName("getProfile : lève EntityNotFoundException si l'utilisateur n'existe pas")
    void getProfile_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfile(EMAIL))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // ---------- updateProfile : validations ----------

    @Test
    @DisplayName("updateProfile : lève IllegalArgumentException si aucun champ n'est renseigné")
    void updateProfile_shouldThrow_whenNoFieldProvided() {
        assertThatThrownBy(() -> userService.updateProfile(EMAIL, request))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(userRepository, passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("updateProfile : lève EntityNotFoundException si l'utilisateur n'existe pas")
    void updateProfile_shouldThrow_whenUserNotFound() {
        request.setUsername("nouveau");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateProfile(EMAIL, request))
                .isInstanceOf(EntityNotFoundException.class);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(jwtService);
    }

    // ---------- updateProfile : email ----------

    @Test
    @DisplayName("updateProfile : met à jour l'email et génère un nouveau token")
    void updateProfile_shouldUpdateEmail() {
        request.setEmail("nouveau@test.com");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("nouveau@test.com")).thenReturn(false);
        when(jwtService.generateToken(user)).thenReturn(TOKEN);

        JwtResponse response = userService.updateProfile(EMAIL, request);

        assertThat(response).isNotNull();
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("nouveau@test.com");
        assertThat(captor.getValue().getUsername()).isEqualTo(USERNAME);
        verify(jwtService).generateToken(user);
    }

    @Test
    @DisplayName("updateProfile : lève EmailAlreadyExistsException si le nouvel email est déjà pris")
    void updateProfile_shouldThrow_whenEmailAlreadyExists() {
        request.setEmail("pris@test.com");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("pris@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(EMAIL, request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("updateProfile : ne vérifie pas l'unicité si l'email est identique à l'actuel")
    void updateProfile_shouldNotCheckEmailUniqueness_whenEmailUnchanged() {
        request.setEmail(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(TOKEN);

        userService.updateProfile(EMAIL, request);

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository).save(user);
    }

    // ---------- updateProfile : username ----------

    @Test
    @DisplayName("updateProfile : met à jour le username")
    void updateProfile_shouldUpdateUsername() {
        request.setUsername("nouveau");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("nouveau")).thenReturn(false);
        when(jwtService.generateToken(user)).thenReturn(TOKEN);

        userService.updateProfile(EMAIL, request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getUsername()).isEqualTo("nouveau");
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("updateProfile : lève UsernameAlreadyExistsException si le nouveau username est déjà pris")
    void updateProfile_shouldThrow_whenUsernameAlreadyExists() {
        request.setUsername("pris");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("pris")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(EMAIL, request))
                .isInstanceOf(UsernameAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("updateProfile : ne vérifie pas l'unicité si le username est identique à l'actuel")
    void updateProfile_shouldNotCheckUsernameUniqueness_whenUsernameUnchanged() {
        request.setUsername(USERNAME);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(TOKEN);

        userService.updateProfile(EMAIL, request);

        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository).save(user);
    }

    // ---------- updateProfile : mot de passe ----------

    @Test
    @DisplayName("updateProfile : encode le nouveau mot de passe avant de le sauvegarder")
    void updateProfile_shouldEncodePassword() {
        request.setPassword("NewPassword1!");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NewPassword1!")).thenReturn("encoded-new");
        when(jwtService.generateToken(user)).thenReturn(TOKEN);

        userService.updateProfile(EMAIL, request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-new");
        assertThat(captor.getValue().getPassword()).isNotEqualTo("NewPassword1!");
    }

    @Test
    @DisplayName("updateProfile : ne touche pas au mot de passe s'il n'est pas fourni")
    void updateProfile_shouldKeepPassword_whenNotProvided() {
        request.setUsername("nouveau");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("nouveau")).thenReturn(false);
        when(jwtService.generateToken(user)).thenReturn(TOKEN);

        userService.updateProfile(EMAIL, request);

        verifyNoInteractions(passwordEncoder);
        assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);
    }

    // ---------- updateProfile : combinaison ----------

    @Test
    @DisplayName("updateProfile : met à jour plusieurs champs en une seule fois")
    void updateProfile_shouldUpdateAllFields() {
        request.setEmail("nouveau@test.com");
        request.setUsername("nouveau");
        request.setPassword("NewPassword1!");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("nouveau@test.com")).thenReturn(false);
        when(userRepository.existsByUsername("nouveau")).thenReturn(false);
        when(passwordEncoder.encode("NewPassword1!")).thenReturn("encoded-new");
        when(jwtService.generateToken(user)).thenReturn(TOKEN);

        userService.updateProfile(EMAIL, request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("nouveau@test.com");
        assertThat(saved.getUsername()).isEqualTo("nouveau");
        assertThat(saved.getPassword()).isEqualTo("encoded-new");
    }
}