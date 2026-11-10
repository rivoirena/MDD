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

/**
 * Service de gestion des abonnements d'un utilisateur aux thèmes.
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final ThemeMapper themeMapper;

    /**
     * Retourne les thèmes auxquels l'utilisateur est abonné.
     *
     * @param email email de l'utilisateur connecté
     * @return la liste des thèmes (tous marqués comme abonnés)
     * @throws EntityNotFoundException si l'utilisateur n'existe pas
     */
    @Transactional(readOnly = true)
    public List<ThemeResponse> getSubscriptions(String email) {
        return findUser(email).getSubscriptions().stream()
                .map(theme -> themeMapper.toResponse(theme, true))
                .toList();
    }

    /**
     * Abonne l'utilisateur à un thème.
     *
     * @param email   email de l'utilisateur connecté
     * @param themeId identifiant du thème
     * @throws EntityNotFoundException si l'utilisateur ou le thème n'existe pas
     */
    @Transactional
    public void subscribe(String email, Long themeId) {
        findUser(email).getSubscriptions().add(findTheme(themeId));
    }

    /**
     * Désabonne l'utilisateur d'un thème.
     *
     * @param email   email de l'utilisateur connecté
     * @param themeId identifiant du thème
     * @throws EntityNotFoundException si l'utilisateur ou le thème n'existe pas
     */
    @Transactional
    public void unsubscribe(String email, Long themeId) {
        findUser(email).getSubscriptions().remove(findTheme(themeId));
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
    }

    private Theme findTheme(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Thème introuvable"));
    }
}