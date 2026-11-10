package com.mdd.controllers;

import com.mdd.dto.CommentDto;
import com.mdd.exception.GlobalExceptionHandler;
import com.mdd.mapper.CommentMapper;
import com.mdd.models.Comment;
import com.mdd.payload.request.CreateCommentRequest;
import com.mdd.security.JwtAuthenticationFilter;
import com.mdd.security.JwtService;
import com.mdd.security.WebSecurityConfig;
import com.mdd.services.CommentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import({WebSecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class CommentControllerTest {

    private static final String EMAIL = "user@test.com";
    private static final Long ARTICLE_ID = 1L;
    private static final String URL = "/api/articles/" + ARTICLE_ID + "/comments";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CommentService commentService;
    @MockitoBean private CommentMapper commentMapper;

    // Beans requis par la configuration de sécurité importée
    @MockitoBean private JwtService jwtService;

    private UsernamePasswordAuthenticationToken authenticatedUser() {
        return new UsernamePasswordAuthenticationToken(EMAIL, null, List.of());
    }

    private CreateCommentRequest validRequest() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Super article !");
        return request;
    }

    // ---------- GET /api/articles/{articleId}/comments ----------

    @Test
    void getComments_returnsMappedComments() throws Exception {
        Comment comment = new Comment();
        CommentDto dto = new CommentDto(
                5L, "Super article !", 2L, "alice", ARTICLE_ID,
                LocalDateTime.of(2026, 1, 15, 10, 30));

        when(commentService.findByArticleId(ARTICLE_ID)).thenReturn(List.of(comment));
        when(commentMapper.toDto(List.of(comment))).thenReturn(List.of(dto));

        mockMvc.perform(get(URL).with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].content").value("Super article !"))
                .andExpect(jsonPath("$[0].authorId").value(2))
                .andExpect(jsonPath("$[0].authorName").value("alice"))
                .andExpect(jsonPath("$[0].articleId").value(ARTICLE_ID))
                .andExpect(jsonPath("$[0].createdAt").exists());
    }

    @Test
    void getComments_whenNoComments_returnsEmptyArray() throws Exception {
        when(commentService.findByArticleId(ARTICLE_ID)).thenReturn(List.of());
        when(commentMapper.toDto(List.<Comment>of())).thenReturn(List.of());

        mockMvc.perform(get(URL).with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getComments_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).findByArticleId(any());
    }

    // ---------- POST /api/articles/{articleId}/comments ----------

    @Test
    void create_returns201WithCreatedComment() throws Exception {
        Comment created = new Comment();
        CommentDto dto = new CommentDto();
        dto.setContent("Super article !");

        when(commentService.create(eq(ARTICLE_ID), any(CreateCommentRequest.class), eq(EMAIL)))
                .thenReturn(created);
        when(commentMapper.toDto(created)).thenReturn(dto);

        mockMvc.perform(post(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Super article !"));
    }

    @Test
    void create_passesAuthenticatedEmailAndArticleIdToService() throws Exception {
        Comment created = new Comment();
        when(commentService.create(eq(ARTICLE_ID), any(CreateCommentRequest.class), eq(EMAIL)))
                .thenReturn(created);
        when(commentMapper.toDto(created)).thenReturn(new CommentDto());

        mockMvc.perform(post(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated());

        verify(commentService).create(eq(ARTICLE_ID), any(CreateCommentRequest.class), eq(EMAIL));
    }

    @Test
    void create_withBlankContent_returns400() throws Exception {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("   ");

        mockMvc.perform(post(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).create(any(), any(), any());
    }

    @Test
    void create_withMissingContent_returns400() throws Exception {
        mockMvc.perform(post(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).create(any(), any(), any());
    }

    @Test
    void create_withUnknownArticle_returns404() throws Exception {
        when(commentService.create(eq(ARTICLE_ID), any(CreateCommentRequest.class), eq(EMAIL)))
                .thenThrow(new EntityNotFoundException("Article introuvable"));

        mockMvc.perform(post(URL)
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Article introuvable")));
    }

    @Test
    void create_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).create(any(), any(), any());
    }
}