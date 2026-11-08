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

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public List<Article> findForUser(String email, Sort.Direction direction) {
        return articleRepository.findBySubscriber(email, Sort.by(direction, "createdAt"));
    }

    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id);
    }

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