package com.mdd.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entité JPA représentant un thème (sujet de programmation) auquel les utilisateurs
 * peuvent s'abonner et dans lequel les articles sont publiés.
 */
@Entity
@Table(name = "themes")
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Titre du thème, unique (100 caractères maximum). */
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String title;

    /** Description du thème (texte long). */
    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}