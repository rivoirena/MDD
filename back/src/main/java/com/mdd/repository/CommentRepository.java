package com.mdd.repository;

import com.mdd.models.Article;
import com.mdd.models.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Override
    @EntityGraph(attributePaths = {"author"})
    Optional<Comment> findById(Long id);

    List<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId);
}