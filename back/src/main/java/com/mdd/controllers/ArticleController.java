package com.mdd.controllers;

import com.mdd.dto.ArticleDto;
import com.mdd.mapper.ArticleMapper;
import com.mdd.models.Article;
import com.mdd.payload.request.CreateArticleRequest;
import com.mdd.services.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST de gestion des articles.
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    /**
     * Retourne le fil d'actualité de l'utilisateur : les articles des thèmes auxquels il est abonné.
     *
     * @param email email de l'utilisateur connecté (extrait du JWT)
     * @param sort  ordre de tri par date : {@code asc} ou {@code desc} (défaut : {@code desc})
     * @return la liste des articles (200)
     */
    @GetMapping
    public ResponseEntity<List<ArticleDto>> findAll(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "desc") String sort) {
        Sort.Direction direction = Sort.Direction.fromString(sort);
        return ResponseEntity.ok(articleMapper.toDto(articleService.findForUser(email, direction)));
    }

    /**
     * Retourne un article par son identifiant.
     *
     * @return l'article (200)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> findById(@PathVariable Long id) {
        return articleService.findById(id)
                .map(article -> ResponseEntity.ok(articleMapper.toDto(article)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un article au nom de l'utilisateur connecté.
     *
     * @param email email de l'utilisateur connecté (extrait du JWT)
     * @return l'article créé (201)
     */
    @PostMapping
    public ResponseEntity<ArticleDto> create(@Valid @RequestBody CreateArticleRequest request,
                                             @AuthenticationPrincipal String email) {
        Article created = articleService.create(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleMapper.toDto(created));
    }
}
