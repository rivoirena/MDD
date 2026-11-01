package com.mdd.controllers;

import com.mdd.dto.ArticleDto;
import com.mdd.mapper.ArticleMapper;
import com.mdd.models.Article;
import com.mdd.payload.request.CreateArticleRequest;
import com.mdd.services.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ArticleMapper articleMapper;

    @GetMapping
    public ResponseEntity<List<ArticleDto>> findAll() {
        return ResponseEntity.ok(articleMapper.toDto(articleService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDto> findById(@PathVariable Long id) {
        return articleService.findById(id)
                .map(article -> ResponseEntity.ok(articleMapper.toDto(article)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ArticleDto> create(@Valid @RequestBody CreateArticleRequest request,
                                             @AuthenticationPrincipal String email) {
        Article created = articleService.create(request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleMapper.toDto(created));
    }
}
