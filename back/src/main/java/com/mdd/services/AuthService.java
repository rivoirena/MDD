package com.mdd.services;

import com.mdd.exception.EmailAlreadyExistsException;
import com.mdd.exception.UsernameAlreadyExistsException;
import com.mdd.models.User;
import com.mdd.payload.request.RegisterRequest;
import com.mdd.payload.response.JwtResponse;
import com.mdd.security.JwtService;
import com.mdd.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        if (userService.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userService.save(user);

        return new JwtResponse(jwtService.generateToken(savedUser));
    }
}