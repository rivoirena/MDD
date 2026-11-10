package com.mdd.services;

import com.mdd.mapper.ThemeMapper;
import com.mdd.models.Theme;
import com.mdd.models.User;
import com.mdd.payload.response.ThemeResponse;
import com.mdd.repository.ThemeRepository;
import com.mdd.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

    private static final String EMAIL = "user@mdd.com";

    @Mock private ThemeRepository themeRepository;
    @Mock private UserRepository userRepository;
    @Mock private ThemeMapper themeMapper;

    @InjectMocks private ThemeService themeService;

    @Test
    @DisplayName("getAllThemes : subscribed=true seulement pour les thèmes auxquels l'utilisateur est abonné")
    void getAllThemes_shouldFlagSubscribedThemes() {
        Theme java = Theme.builder().id(1L).title("Java").description("d1").build();
        Theme angular = Theme.builder().id(2L).title("Angular").description("d2").build();
        User user = User.builder().email(EMAIL).subscriptions(new HashSet<>(Set.of(java))).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeRepository.findAll()).thenReturn(List.of(java, angular));
        when(themeMapper.toResponse(java, true)).thenReturn(new ThemeResponse(1L, "Java", "d1", true));
        when(themeMapper.toResponse(angular, false)).thenReturn(new ThemeResponse(2L, "Angular", "d2", false));

        List<ThemeResponse> result = themeService.getAllThemes(EMAIL);

        assertThat(result).extracting(ThemeResponse::getId, ThemeResponse::isSubscribed)
                .containsExactly(tuple(1L, true), tuple(2L, false));
    }

    @Test
    @DisplayName("getAllThemes : tout à false si l'utilisateur n'a aucun abonnement")
    void getAllThemes_shouldReturnAllUnsubscribed_whenNoSubscriptions() {
        Theme java = Theme.builder().id(1L).title("Java").description("d1").build();
        User user = User.builder().email(EMAIL).build(); // subscriptions = HashSet vide (@Builder.Default)

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeRepository.findAll()).thenReturn(List.of(java));
        when(themeMapper.toResponse(java, false)).thenReturn(new ThemeResponse(1L, "Java", "d1", false));

        assertThat(themeService.getAllThemes(EMAIL))
                .extracting(ThemeResponse::isSubscribed)
                .containsExactly(false);
    }

    @Test
    @DisplayName("getAllThemes : liste vide s'il n'y a aucun thème")
    void getAllThemes_shouldReturnEmptyList_whenNoThemes() {
        User user = User.builder().email(EMAIL).build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(themeRepository.findAll()).thenReturn(List.of());

        assertThat(themeService.getAllThemes(EMAIL)).isEmpty();
        verifyNoInteractions(themeMapper);
    }

    @Test
    @DisplayName("getAllThemes : EntityNotFoundException si l'utilisateur n'existe pas")
    void getAllThemes_shouldThrow_whenUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> themeService.getAllThemes(EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Utilisateur introuvable");

        verifyNoInteractions(themeRepository, themeMapper);
    }
}