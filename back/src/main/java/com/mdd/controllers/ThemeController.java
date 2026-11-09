package com.mdd.controllers;

import com.mdd.payload.response.ThemeResponse;
import com.mdd.services.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST de consultation des thèmes.
 */
@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
public class ThemeController {

    private final ThemeService themeService;

    /**
     * Retourne la liste de tous les thèmes.
     *
     * @param email email de l'utilisateur connecté (extrait du JWT)
     * @return la liste des thèmes (200)
     */
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getAllThemes(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(themeService.getAllThemes(email));
    }
}
