package com.mdd.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entité JPA représentant un utilisateur de l'application.
 * <p>
 * Un utilisateur s'abonne à des {@link Theme} ; ces abonnements déterminent
 * les articles affichés dans son fil d'actualité.
 */
@Entity
@Table(name = "users")
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"password", "subscriptions"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Adresse email, unique, utilisée pour se connecter. */
    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    /** Nom d'utilisateur affiché, unique (50 caractères maximum), utilisé pour se connecter.. */
    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Hash BCrypt du mot de passe (jamais le mot de passe en clair).
     * La validation du mot de passe en clair est faite sur le DTO d'inscription.
     */
    @NotBlank
    @Column(nullable = false, length = 255)
    private String password;

    /** Date de création du compte, renseignée automatiquement et non modifiable. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Date de dernière modification du compte, mise à jour automatiquement. */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Thèmes auxquels l'utilisateur est abonné (table de jointure {@code subscriptions}). */
    @ManyToMany
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    @Builder.Default
    private Set<Theme> subscriptions = new HashSet<>();
}