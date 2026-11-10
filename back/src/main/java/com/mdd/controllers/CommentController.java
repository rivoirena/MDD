package com.mdd.controllers;

import com.mdd.dto.CommentDto;
import com.mdd.mapper.CommentMapper;
import com.mdd.models.Comment;
import com.mdd.payload.request.CreateCommentRequest;
import com.mdd.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST de gestion des commentaires d'un article.
 */
@RestController
@RequestMapping("/api/articles/{articleId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    /**
     * Retourne les commentaires d'un article.
     *
     * @return la liste des commentaires (200)
     */
    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long articleId) {
        return ResponseEntity.ok(commentMapper.toDto(commentService.findByArticleId(articleId)));
    }

    /**
     * Ajoute un commentaire à un article au nom de l'utilisateur connecté.
     *
     * @param email email de l'utilisateur connecté (extrait du JWT)
     * @return le commentaire créé (201)
     */
    @PostMapping
    public ResponseEntity<CommentDto> create(@PathVariable Long articleId,
                                             @Valid @RequestBody CreateCommentRequest request,
                                             @AuthenticationPrincipal String email) {
        Comment created = commentService.create(articleId, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentMapper.toDto(created));
    }
}