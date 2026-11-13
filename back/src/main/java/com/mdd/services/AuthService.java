package com.mdd.services;

import com.mdd.exception.EmailAlreadyExistsException;
import com.mdd.exception.InvalidCredentialsException;
import com.mdd.exception.UsernameAlreadyExistsException;
import com.mdd.models.User;
import com.mdd.payload.request.LoginRequest;
import com.mdd.payload.request.RegisterRequest;
import com.mdd.payload.response.JwtResponse;
import com.mdd.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service d'authentification : inscription et connexion, avec émission d'un token JWT.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Inscrit un nouvel utilisateur (mot de passe encodé) et retourne un token JWT.
     *
     * @param request données d'inscription (email, nom d'utilisateur, mot de passe)
     * @return la réponse contenant le token JWT
     * @throws EmailAlreadyExistsException    si l'email est déjà utilisé
     * @throws UsernameAlreadyExistsException si le nom d'utilisateur est déjà utilisé
     */
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

    /**
     * Authentifie un utilisateur par email ou nom d'utilisateur et retourne un token JWT.
     *
     * @param request identifiant (email ou nom d'utilisateur) et mot de passe
     * @return la réponse contenant le token JWT
     * @throws InvalidCredentialsException si l'identifiant est inconnu ou le mot de passe incorrect
     */
    @Transactional(readOnly = true)
    public JwtResponse login(LoginRequest request) {
        User user = userService.findByEmailOrUsername(request.getIdentifier())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return new JwtResponse(jwtService.generateToken(user));
    }
}