package com.mdd.controllers;

import com.mdd.payload.response.ThemeResponse;
import com.mdd.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST de gestion des abonnements de l'utilisateur connecté.
 */
@RestController
@RequestMapping("/api/users/me/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * Retourne les thèmes auxquels l'utilisateur est abonné.
     *
     * @param email email de l'utilisateur connecté (extrait du JWT)
     * @return la liste des thèmes (200)
     */
    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getMySubscriptions(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(subscriptionService.getSubscriptions(email));
    }

    /**
     * Abonne l'utilisateur à un thème.
     *
     * @param email email de l'utilisateur connecté (extrait du JWT)
     * @return aucun contenu (204)
     */
    @PostMapping("/{themeId}")
    public ResponseEntity<Void> subscribe(@PathVariable Long themeId,
                                          @AuthenticationPrincipal String email) {
        subscriptionService.subscribe(email, themeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Désabonne l'utilisateur d'un thème.
     *
     * @param email email de l'utilisateur connecté (extrait du JWT)
     * @return aucun contenu (204)
     */
    @DeleteMapping("/{themeId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long themeId,
                                            @AuthenticationPrincipal String email) {
        subscriptionService.unsubscribe(email, themeId);
        return ResponseEntity.noContent().build();
    }
}
