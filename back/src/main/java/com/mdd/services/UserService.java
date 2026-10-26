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

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ThemeMapper themeMapper;

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public Optional<User> findByEmailOrUsername(String identifier) {
        return userRepository.findByEmailOrUsername(identifier, identifier);
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(String currentEmail) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        return new UserResponse(user.getEmail(), user.getUsername());
    }

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

    @Transactional(readOnly = true)
    public List<ThemeResponse> getSubscriptions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
        return themeMapper.toResponseList(user.getSubscriptions());
    }
}
