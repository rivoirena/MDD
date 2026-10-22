package com.mdd.repository;

import com.mdd.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId);
}