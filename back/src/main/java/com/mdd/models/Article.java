package com.mdd.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entité JPA représentant un article publié par un utilisateur dans un thème.
 * <p>
 * Un article appartient à un auteur ({@link User}) et à un thème ({@link Theme}) ;
 * ces deux associations sont chargées en différé ({@code LAZY}).
 */
@Entity
@Table(name = "articles")
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"author", "theme"})
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre de l'article (255 caractères maximum). */
    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    /** Contenu de l'article (texte long). */
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Date de création, renseignée automatiquement à l'insertion et non modifiable. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Auteur de l'article. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /** Thème auquel l'article est rattaché. */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;
}