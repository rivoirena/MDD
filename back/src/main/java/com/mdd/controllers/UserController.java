package com.mdd.controllers;

import com.mdd.payload.request.UpdateProfileRequest;
import com.mdd.payload.response.JwtResponse;
import com.mdd.payload.response.UserResponse;
import com.mdd.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST de gestion du profil de l'utilisateur connecté.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retourne le profil de l'utilisateur connecté.
     *
     * @param email email de l'utilisateur connecté (extrait du JWT)
     * @return le profil (200)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(userService.getProfile(email));
    }

    /**
     * Met à jour le profil de l'utilisateur connecté.
     *
     * @param email email de l'utilisateur connecté (extrait du JWT)
     * @return un nouveau JWT reflétant le profil mis à jour (200)
     */
    @PutMapping("/me")
    public ResponseEntity<JwtResponse> updateMe(@AuthenticationPrincipal String email,
                                @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(email, request));
    }
}