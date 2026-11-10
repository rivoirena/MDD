package com.mdd.services;

import com.mdd.mapper.ThemeMapper;
import com.mdd.models.Theme;
import com.mdd.models.User;
import com.mdd.payload.response.ThemeResponse;
import com.mdd.repository.ThemeRepository;
import com.mdd.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    private static final String EMAIL = "user@test.com";

    @Mock private UserRepository userRepository;
    @Mock private ThemeRepository themeRepository;
    @Mock private ThemeMapper themeMapper;

    @InjectMocks private SubscriptionService subscriptionService;

    private Theme java;
    private Theme angular;

    @BeforeEach
    void setUp() {
        java = Theme.builder().id(1L).title("Java").description("d1").build();
        angular = Theme.builder().id(2L).title("Angular").description("d2").build();
    }

    // ---------- getSubscriptions ----------

    @Test
    @DisplayName("getSubscriptions : retourne les thèmes de l'utilisateur, tous marqués abonnés")
    void getSubscriptions_shouldReturnSubscribedThemes() {
        User user = User.builder().email(EMAIL).subscriptions(new HashSet<>(Set.of(java))).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeMapper.toResponse(java, true))
                .thenReturn(new ThemeResponse(1L, "Java", "d1", true));

        List<ThemeResponse> result = subscriptionService.getSubscriptions(EMAIL);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isSubscribed()).isTrue();
    }

    @Test
    @DisplayName("getSubscriptions : liste vide si aucun abonnement")
    void getSubscriptions_shouldReturnEmptyList_whenNoSubscriptions() {
        User user = User.builder().email(EMAIL).subscriptions(new HashSet<>()).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThat(subscriptionService.getSubscriptions(EMAIL)).isEmpty();
        verifyNoInteractions(themeMapper);
    }

    @Test
    @DisplayName("getSubscriptions : EntityNotFoundException si l'utilisateur n'existe pas")
    void getSubscriptions_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.getSubscriptions(EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Utilisateur introuvable");
    }

    // ---------- subscribe ----------

    @Test
    @DisplayName("subscribe : ajoute le thème aux abonnements de l'utilisateur")
    void subscribe_shouldAddThemeToUserSubscriptions() {
        User user = User.builder().email(EMAIL).subscriptions(new HashSet<>()).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeRepository.findById(1L)).thenReturn(Optional.of(java));

        subscriptionService.subscribe(EMAIL, 1L);

        assertThat(user.getSubscriptions()).containsExactly(java);
    }

    @Test
    @DisplayName("subscribe : idempotent si l'utilisateur est déjà abonné")
    void subscribe_shouldNotDuplicate_whenAlreadySubscribed() {
        User user = User.builder().email(EMAIL).subscriptions(new HashSet<>(Set.of(java))).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeRepository.findById(1L)).thenReturn(Optional.of(java));

        subscriptionService.subscribe(EMAIL, 1L);

        assertThat(user.getSubscriptions()).hasSize(1);
    }

    @Test
    @DisplayName("subscribe : EntityNotFoundException si l'utilisateur n'existe pas")
    void subscribe_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.subscribe(EMAIL, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Utilisateur introuvable");

        verifyNoInteractions(themeRepository);
    }

    @Test
    @DisplayName("subscribe : EntityNotFoundException si le thème n'existe pas")
    void subscribe_shouldThrow_whenThemeNotFound() {
        User user = User.builder().email(EMAIL).subscriptions(new HashSet<>()).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.subscribe(EMAIL, 99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Thème introuvable");

        assertThat(user.getSubscriptions()).isEmpty();
    }

    // ---------- unsubscribe ----------

    @Test
    @DisplayName("unsubscribe : retire le thème des abonnements, sans toucher aux autres")
    void unsubscribe_shouldRemoveOnlyTargetTheme() {
        User user = User.builder().email(EMAIL)
                .subscriptions(new HashSet<>(Set.of(java, angular))).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeRepository.findById(1L)).thenReturn(Optional.of(java));

        subscriptionService.unsubscribe(EMAIL, 1L);

        assertThat(user.getSubscriptions()).containsExactly(angular);
    }

    @Test
    @DisplayName("unsubscribe : sans effet si l'utilisateur n'était pas abonné")
    void unsubscribe_shouldDoNothing_whenNotSubscribed() {
        User user = User.builder().email(EMAIL).subscriptions(new HashSet<>(Set.of(angular))).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeRepository.findById(1L)).thenReturn(Optional.of(java));

        subscriptionService.unsubscribe(EMAIL, 1L);

        assertThat(user.getSubscriptions()).containsExactly(angular);
    }

    @Test
    @DisplayName("unsubscribe : EntityNotFoundException si l'utilisateur n'existe pas")
    void unsubscribe_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.unsubscribe(EMAIL, 1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Utilisateur introuvable");
    }

    @Test
    @DisplayName("unsubscribe : EntityNotFoundException si le thème n'existe pas")
    void unsubscribe_shouldThrow_whenThemeNotFound() {
        User user = User.builder().email(EMAIL).subscriptions(new HashSet<>(Set.of(java))).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.unsubscribe(EMAIL, 99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Thème introuvable");

        assertThat(user.getSubscriptions()).containsExactly(java);
    }
}