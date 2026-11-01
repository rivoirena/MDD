package com.mdd.services;

import com.mdd.models.Article;
import com.mdd.models.Comment;
import com.mdd.models.User;
import com.mdd.payload.request.CreateCommentRequest;
import com.mdd.repository.ArticleRepository;
import com.mdd.repository.CommentRepository;
import com.mdd.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public List<Comment> findByArticleId(Long articleId) {
        return commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId);
    }

    @Transactional
    public Comment create(Long articleId, CreateCommentRequest request, String email) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article introuvable"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .author(author)
                .article(article)
                .build();

        return commentRepository.save(comment);
    }
}