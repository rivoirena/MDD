package com.mdd.controller;

import com.mdd.dto.CommentDto;
import com.mdd.mapper.CommentMapper;
import com.mdd.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long articleId) {
        return ResponseEntity.ok(commentMapper.toDto(commentService.findByArticleId(articleId)));
    }
}