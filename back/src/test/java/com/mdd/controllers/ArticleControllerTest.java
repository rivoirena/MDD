package com.mdd.controllers;

import com.mdd.dto.ArticleDto;
import com.mdd.exception.GlobalExceptionHandler;
import com.mdd.mapper.ArticleMapper;
import com.mdd.models.Article;
import com.mdd.payload.request.CreateArticleRequest;
import com.mdd.security.JwtAuthenticationFilter;
import com.mdd.security.JwtService;
import com.mdd.security.WebSecurityConfig;
import com.mdd.services.ArticleService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticleController.class)
@Import({WebSecurityConfig.class, JwtAuthenticationFilter.class, GlobalExceptionHandler.class})
class ArticleControllerTest {

    private static final String EMAIL = "alice@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArticleService articleService;

    @MockitoBean
    private ArticleMapper articleMapper;

    @MockitoBean
    private JwtService jwtService;

    /** Simule un utilisateur connecté dont le principal est son email (String). */
    private static UsernamePasswordAuthenticationToken authenticatedUser() {
        return new UsernamePasswordAuthenticationToken(EMAIL, null, List.of());
    }

    private static CreateArticleRequest validRequest() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("Mon titre");
        request.setContent("Mon contenu");
        request.setThemeId(1L);
        return request;
    }

    // ---------- GET /api/articles ----------

    @Test
    void findAll_defaultsToDescSort() throws Exception {
        Article article = new Article();
        ArticleDto dto = new ArticleDto();
        dto.setTitle("Titre");
        when(articleService.findForUser(EMAIL, Sort.Direction.DESC)).thenReturn(List.of(article));
        when(articleMapper.toDto(List.of(article))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/articles").with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Titre"));

        verify(articleService).findForUser(EMAIL, Sort.Direction.DESC);
    }

    @Test
    void findAll_withAscSort_usesAscDirection() throws Exception {
        when(articleService.findForUser(EMAIL, Sort.Direction.ASC)).thenReturn(List.of());
        when(articleMapper.toDto(List.<Article>of())).thenReturn(List.of());

        mockMvc.perform(get("/api/articles")
                        .param("sort", "asc")
                        .with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(articleService).findForUser(EMAIL, Sort.Direction.ASC);
    }

    @Test
    void findAll_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isUnauthorized());

        verify(articleService, never()).findForUser(any(), any());
    }

    // ---------- GET /api/articles/{id} ----------

    @Test
    void findById_whenFound_returns200() throws Exception {
        Article article = new Article();
        ArticleDto dto = new ArticleDto();
        dto.setTitle("Titre");
        when(articleService.findById(1L)).thenReturn(Optional.of(article));
        when(articleMapper.toDto(article)).thenReturn(dto);

        mockMvc.perform(get("/api/articles/1").with(authentication(authenticatedUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Titre"));
    }

    @Test
    void findById_whenNotFound_returns404() throws Exception {
        when(articleService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/articles/99").with(authentication(authenticatedUser())))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isUnauthorized());
    }

    // ---------- POST /api/articles ----------

    @Test
    void create_withValidRequest_returns201() throws Exception {
        CreateArticleRequest request = validRequest();
        Article created = new Article();
        ArticleDto dto = new ArticleDto();
        dto.setTitle("Mon titre");
        when(articleService.create(any(CreateArticleRequest.class), eq(EMAIL))).thenReturn(created);
        when(articleMapper.toDto(created)).thenReturn(dto);

        mockMvc.perform(post("/api/articles")
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Mon titre"));

        verify(articleService).create(any(CreateArticleRequest.class), eq(EMAIL));
    }

    @Test
    void create_withInvalidRequest_returns400() throws Exception {
        CreateArticleRequest request = new CreateArticleRequest(); // champs obligatoires absents

        mockMvc.perform(post("/api/articles")
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any(), any());
    }

    @Test
    void create_withUnknownTheme_returns404() throws Exception {
        when(articleService.create(any(CreateArticleRequest.class), eq(EMAIL)))
                .thenThrow(new EntityNotFoundException("Thème introuvable"));

        mockMvc.perform(post("/api/articles")
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Thème")));
    }

    @Test
    void create_withMalformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/articles")
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ pas du json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed or missing request body"));
    }

    @Test
    void create_withoutAuthentication_returns401() throws Exception {
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isUnauthorized());

        verify(articleService, never()).create(any(), any());
    }

    @Test
    void create_withTitleTooLong_returns400() throws Exception {
        CreateArticleRequest request = validRequest();
        request.setTitle("a".repeat(256));

        mockMvc.perform(post("/api/articles")
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(articleService, never()).create(any(), any());
    }

    @Test
    void create_withTitleAtMaxLength_returns201() throws Exception {
        CreateArticleRequest request = validRequest();
        request.setTitle("a".repeat(255));
        Article created = new Article();
        when(articleService.create(any(CreateArticleRequest.class), eq(EMAIL))).thenReturn(created);
        when(articleMapper.toDto(created)).thenReturn(new ArticleDto());

        mockMvc.perform(post("/api/articles")
                        .with(authentication(authenticatedUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}