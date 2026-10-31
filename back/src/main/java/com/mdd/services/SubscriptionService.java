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

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final ThemeMapper themeMapper;

    @Transactional(readOnly = true)
    public List<ThemeResponse> getSubscriptions(String email) {
        return findUser(email).getSubscriptions().stream()
                .map(theme -> themeMapper.toResponse(theme, true))
                .toList();
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
    }
}