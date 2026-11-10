package com.mdd.repository;

import com.mdd.models.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité {@link Comment}.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Recherche un commentaire par son identifiant, auteur inclus.
     *
     * @param id l'identifiant du commentaire
     * @return le commentaire, ou un {@code Optional} vide s'il n'existe pas
     */
    @Override
    @EntityGraph(attributePaths = {"author"})
    Optional<Comment> findById(Long id);

    /**
     * Retourne les commentaires d'un article, du plus récent au plus ancien.
     *
     * @param articleId l'identifiant de l'article
     * @return la liste des commentaires, vide si l'article n'en a pas
     */
    List<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId);
}