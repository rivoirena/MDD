package com.mdd.services;

import com.mdd.exception.EmailAlreadyExistsException;
import com.mdd.exception.UsernameAlreadyExistsException;
import com.mdd.models.User;
import com.mdd.payload.request.RegisterRequest;
import com.mdd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
