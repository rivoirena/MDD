package com.mdd.services;

import com.mdd.models.Article;
import com.mdd.models.Theme;
import com.mdd.models.User;
import com.mdd.payload.request.CreateArticleRequest;
import com.mdd.repository.ArticleRepository;
import com.mdd.repository.ThemeRepository;
import com.mdd.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des articles : consultation (filtrée par abonnements) et création.
 */
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;

    /**
     * Retourne tous les articles, sans filtre.
     *
     * @return la liste de tous les articles
     */
    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    /**
     * Retourne les articles dont le thème fait partie des abonnements de l'utilisateur,
     * triés par date de création.
     *
     * @param email     email de l'utilisateur connecté
     * @param direction sens du tri ({@code ASC} ou {@code DESC})
     * @return la liste des articles correspondants
     */
    public List<Article> findForUser(String email, Sort.Direction direction) {
        return articleRepository.findBySubscriber(email, Sort.by(direction, "createdAt"));
    }

    /**
     * Recherche un article par son identifiant.
     *
     * @param id identifiant de l'article
     * @return l'article, ou un {@link Optional} vide s'il n'existe pas
     */
    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

    /**
     * Crée un article pour l'utilisateur connecté dans le thème demandé.
     *
     * @param request données de l'article (titre, contenu, identifiant du thème)
     * @param email   email de l'auteur
     * @return l'article créé
     * @throws EntityNotFoundException si l'utilisateur ou le thème n'existe pas
     */
    @Transactional
    public Article create(CreateArticleRequest request, String email) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

        Theme theme = themeRepository.findById(request.getThemeId())
                .orElseThrow(() -> new EntityNotFoundException("Thème introuvable"));

        Article article = Article.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .theme(theme)
                .build();

        return articleRepository.save(article);
    }
}