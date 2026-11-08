package com.mdd.repository;

import com.mdd.models.Article;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Override
    @EntityGraph(attributePaths = {"author", "theme"})
    Optional<Article> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"author", "theme"})
    List<Article> findAll();
}
