package com.mdd.repository;

import com.mdd.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité {@link User}.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son adresse email.
     *
     * @param email l'adresse email recherchée
     * @return l'utilisateur, ou un {@code Optional} vide s'il n'existe pas
     */
    Optional<User> findByEmail(String email);

    /**
     * Indique si une adresse email est déjà utilisée.
     *
     * @param email l'adresse email à tester
     * @return {@code true} si un compte existe avec cet email
     */
    boolean existsByEmail(String email);

    /**
     * Indique si un nom d'utilisateur est déjà utilisé.
     *
     * @param username le nom d'utilisateur à tester
     * @return {@code true} si un compte existe avec ce nom d'utilisateur
     */
    boolean existsByUsername(String username);

    /**
     * Recherche un utilisateur par email ou par nom d'utilisateur.
     * <p>
     * Utilisée pour la connexion, où l'identifiant saisi peut être l'un ou l'autre.
     *
     * <p>
     * Un email contient toujours un {@code @} et un nom d'utilisateur n'en contient jamais
     * (voir {@code RegisterRequest}), donc la recherche ne peut pas retourner deux comptes.
     *
     * @param email    la valeur testée sur l'email
     * @param username la valeur testée sur le nom d'utilisateur
     * @return l'utilisateur, ou un {@code Optional} vide s'il n'existe pas
     */
    Optional<User> findByEmailOrUsername(String email, String username);
}