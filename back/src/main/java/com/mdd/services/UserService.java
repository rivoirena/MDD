package com.mdd.services;

import com.mdd.exception.EmailAlreadyExistsException;
import com.mdd.exception.UsernameAlreadyExistsException;
import com.mdd.mapper.ThemeMapper;
import com.mdd.models.User;
import com.mdd.payload.request.UpdateProfileRequest;
import com.mdd.payload.response.JwtResponse;
import com.mdd.payload.response.ThemeResponse;
import com.mdd.payload.response.UserResponse;
import com.mdd.repository.UserRepository;
import com.mdd.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des utilisateurs : accès aux données et mise à jour du profil.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ThemeMapper themeMapper;

    /**
     * Enregistre un utilisateur.
     *
     * @param user l'utilisateur à sauvegarder
     * @return l'utilisateur sauvegardé
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * Recherche un utilisateur par email.
     *
     * @param email email recherché
     * @return l'utilisateur, ou un {@link Optional} vide s'il n'existe pas
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Recherche un utilisateur par identifiant.
     *
     * @param id identifiant recherché
     * @return l'utilisateur, ou un {@link Optional} vide s'il n'existe pas
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Indique si un email est déjà utilisé.
     *
     * @param email email à tester
     * @return {@code true} si un utilisateur possède déjà cet email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Indique si un nom d'utilisateur est déjà utilisé.
     *
     * @param username nom d'utilisateur à tester
     * @return {@code true} si un utilisateur possède déjà ce nom
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Recherche un utilisateur par email ou nom d'utilisateur.
     *
     * @param identifier email ou nom d'utilisateur
     * @return l'utilisateur, ou un {@link Optional} vide s'il n'existe pas
     */
    public Optional<User> findByEmailOrUsername(String identifier) {
        return userRepository.findByEmailOrUsername(identifier, identifier);
    }

    /**
     * Retourne le profil (email et nom d'utilisateur) de l'utilisateur connecté.
     *
     * @param currentEmail email de l'utilisateur connecté
     * @return le profil de l'utilisateur
     * @throws EntityNotFoundException si l'utilisateur n'existe pas
     */
    @Transactional(readOnly = true)
    public UserResponse getProfile(String currentEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        return new UserResponse(user.getEmail(), user.getUsername());
    }

    /**
     * Met à jour l'email, le nom d'utilisateur et/ou le mot de passe de l'utilisateur connecté.
     * Seuls les champs renseignés sont modifiés. Un nouveau token JWT est retourné,
     * car l'email (sujet du token) peut avoir changé.
     *
     * @param currentEmail email actuel de l'utilisateur connecté
     * @param request      champs à modifier (au moins un requis)
     * @return la réponse contenant un nouveau token JWT
     * @throws IllegalArgumentException       si aucun champ n'est renseigné
     * @throws EntityNotFoundException        si l'utilisateur n'existe pas
     * @throws EmailAlreadyExistsException    si le nouvel email est déjà utilisé
     * @throws UsernameAlreadyExistsException si le nouveau nom d'utilisateur est déjà utilisé
     */
    @Transactional
    public JwtResponse updateProfile(String currentEmail, UpdateProfileRequest request) {
        if (request.getEmail() == null && request.getUsername() == null && request.getPassword() == null) {
            throw new IllegalArgumentException("Au moins un champ à modifier est requis");
        }

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException();
            }
            user.setEmail(request.getEmail());
        }

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException();
            }
            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        return new JwtResponse(jwtService.generateToken(user));
    }
}
