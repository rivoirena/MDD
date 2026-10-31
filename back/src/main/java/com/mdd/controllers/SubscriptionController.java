package com.mdd.controllers;

import com.mdd.payload.response.ThemeResponse;
import com.mdd.services.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    public ResponseEntity<List<ThemeResponse>> getMySubscriptions(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(subscriptionService.getSubscriptions(email));
    }
}
