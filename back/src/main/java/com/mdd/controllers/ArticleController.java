package com.mdd.controllers;

import com.mdd.dto.ArticleDto;
import com.mdd.mapper.ArticleMapper;
import com.mdd.services.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
