package com.mdd.repository;

import com.mdd.models.Article;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Override
    @EntityGraph(attributePaths = {"author", "theme"})
    Optional<Article> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"author", "theme"})
    List<Article> findAll();

    @EntityGraph(attributePaths = {"author", "theme"})
    @Query("""
        SELECT a FROM Article a
        WHERE a.theme IN (
            SELECT t FROM User u JOIN u.subscriptions t WHERE u.email = :email
        )
        """)
    List<Article> findBySubscriber(@Param("email") String email, Sort sort);
}
