package com.mdd.services;

import com.mdd.mapper.ThemeMapper;
import com.mdd.models.Theme;
import com.mdd.models.User;
import com.mdd.payload.response.ThemeResponse;
import com.mdd.repository.ThemeRepository;
import com.mdd.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service de consultation des thèmes.
 */
@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final ThemeMapper themeMapper;

    /**
     * Retourne tous les thèmes, chacun indiquant si l'utilisateur y est abonné.
     *
     * @param email email de l'utilisateur connecté
     * @return la liste de tous les thèmes avec leur statut d'abonnement
     * @throws EntityNotFoundException si l'utilisateur n'existe pas
     */
    @Transactional(readOnly = true)
    public List<ThemeResponse> getAllThemes(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        Set<Long> subscribedIds = user.getSubscriptions().stream()
                .map(Theme::getId)
                .collect(Collectors.toSet());

        return themeRepository.findAll().stream()
                .map(theme -> themeMapper.toResponse(theme, subscribedIds.contains(theme.getId())))
                .toList();
    }
}
