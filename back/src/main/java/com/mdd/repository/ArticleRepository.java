package com.mdd.repository;

import com.mdd.models.Article;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité {@link Article}.
 * <p>
 * Les associations {@code author} et {@code theme} sont déclarées en chargement différé
 * sur l'entité ; les méthodes ci-dessous les chargent explicitement via un
 * {@link EntityGraph} afin d'éviter les requêtes supplémentaires (problème N+1)
 * lors de la conversion en DTO.
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Recherche un article par son identifiant, auteur et thème inclus.
     *
     * @param id l'identifiant de l'article
     * @return l'article, ou un {@code Optional} vide s'il n'existe pas
     */
    @Override
    @EntityGraph(attributePaths = {"author", "theme"})
    Optional<Article> findById(Long id);

    /**
     * Retourne tous les articles, auteur et thème inclus.
     *
     * @return la liste de tous les articles
     */
    @Override
    @EntityGraph(attributePaths = {"author", "theme"})
    List<Article> findAll();

    /**
     * Retourne les articles des thèmes auxquels un utilisateur est abonné
     * (fil d'actualité), auteur et thème inclus.
     *
     * @param email l'adresse email de l'utilisateur abonné
     * @param sort  le critère de tri à appliquer (par exemple sur {@code createdAt})
     * @return la liste des articles des thèmes suivis, vide si aucun abonnement
     */
    @EntityGraph(attributePaths = {"author", "theme"})
    @Query("""
        SELECT a FROM Article a
        WHERE a.theme IN (
            SELECT t FROM User u JOIN u.subscriptions t WHERE u.email = :email
        )
        """)
    List<Article> findBySubscriber(@Param("email") String email, Sort sort);
}
