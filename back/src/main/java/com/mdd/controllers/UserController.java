package com.mdd.controllers;

import com.mdd.payload.request.UpdateProfileRequest;
import com.mdd.payload.response.JwtResponse;
import com.mdd.services.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/me")
    public JwtResponse updateMe(@AuthenticationPrincipal String email,
                                @Valid @RequestBody UpdateProfileRequest request) {
        return userService.updateProfile(email, request);
    }
}