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

/**
 * Service de gestion des commentaires d'un article.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    /**
     * Retourne les commentaires d'un article, du plus récent au plus ancien.
     *
     * @param articleId identifiant de l'article
     * @return la liste des commentaires (vide si l'article n'existe pas ou n'a aucun commentaire)
     */
    public List<Comment> findByArticleId(Long articleId) {
        return commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId);
    }

    /**
     * Ajoute un commentaire à un article au nom de l'utilisateur connecté.
     *
     * @param articleId identifiant de l'article commenté
     * @param request   contenu du commentaire
     * @param email     email de l'auteur
     * @return le commentaire créé
     * @throws EntityNotFoundException si l'utilisateur ou l'article n'existe pas
     */
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