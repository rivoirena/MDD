package com.mdd.services;

import com.mdd.models.Article;
import com.mdd.models.Theme;
import com.mdd.models.User;
import com.mdd.payload.request.CreateArticleRequest;
import com.mdd.repository.ArticleRepository;
import com.mdd.repository.ThemeRepository;
import com.mdd.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    private static final String EMAIL = "user@test.com";

    @Mock private ArticleRepository articleRepository;
    @Mock private ThemeRepository themeRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ArticleService articleService;

    private User author;
    private Theme theme;

    @BeforeEach
    void setUp() {
        author = User.builder().id(1L).email(EMAIL).build();
        theme = Theme.builder().id(10L).title("Java").build();
    }

    // ---------- findAll ----------

    @Test
    @DisplayName("findAll : retourne tous les articles du repository")
    void findAll_shouldReturnAllArticles() {
        Article a1 = Article.builder().id(1L).title("A1").build();
        Article a2 = Article.builder().id(2L).title("A2").build();

        when(articleRepository.findAll()).thenReturn(List.of(a1, a2));

        assertThat(articleService.findAll()).containsExactly(a1, a2);
    }

    // ---------- findForUser ----------

    @Test
    @DisplayName("findForUser : trie par createdAt en DESC")
    void findForUser_shouldSortByCreatedAtDesc() {
        Article a1 = Article.builder().id(1L).build();
        when(articleRepository.findBySubscriber(any(), any(Sort.class))).thenReturn(List.of(a1));

        List<Article> result = articleService.findForUser(EMAIL, Sort.Direction.DESC);

        ArgumentCaptor<Sort> captor = ArgumentCaptor.forClass(Sort.class);
        verify(articleRepository).findBySubscriber(org.mockito.ArgumentMatchers.eq(EMAIL), captor.capture());

        assertThat(captor.getValue()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdAt"));
        assertThat(result).containsExactly(a1);
    }

    @Test
    @DisplayName("findForUser : trie par createdAt en ASC")
    void findForUser_shouldSortByCreatedAtAsc() {
        when(articleRepository.findBySubscriber(any(), any(Sort.class))).thenReturn(List.of());

        articleService.findForUser(EMAIL, Sort.Direction.ASC);

        ArgumentCaptor<Sort> captor = ArgumentCaptor.forClass(Sort.class);
        verify(articleRepository).findBySubscriber(org.mockito.ArgumentMatchers.eq(EMAIL), captor.capture());

        assertThat(captor.getValue()).isEqualTo(Sort.by(Sort.Direction.ASC, "createdAt"));
    }

    // ---------- findById ----------

    @Test
    @DisplayName("findById : retourne l'article quand il existe")
    void findById_shouldReturnArticle_whenFound() {
        Article article = Article.builder().id(1L).title("A1").build();
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        assertThat(articleService.findById(1L)).contains(article);
    }

    @Test
    @DisplayName("findById : Optional vide quand l'article n'existe pas")
    void findById_shouldReturnEmpty_whenNotFound() {
        when(articleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(articleService.findById(99L)).isEmpty();
    }

    // ---------- create ----------

    @Test
    @DisplayName("create : sauvegarde un article avec titre, contenu, auteur et thème")
    void create_shouldSaveArticleWithAllFields() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setTitle("Mon titre");
        request.setContent("Mon contenu");
        request.setThemeId(10L);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(author));
        when(themeRepository.findById(10L)).thenReturn(Optional.of(theme));
        when(articleRepository.save(any(Article.class))).thenAnswer(inv -> inv.getArgument(0));

        Article result = articleService.create(request, EMAIL);

        ArgumentCaptor<Article> captor = ArgumentCaptor.forClass(Article.class);
        verify(articleRepository).save(captor.capture());
        Article saved = captor.getValue();

        assertThat(saved.getTitle()).isEqualTo("Mon titre");
        assertThat(saved.getContent()).isEqualTo("Mon contenu");
        assertThat(saved.getAuthor()).isSameAs(author);
        assertThat(saved.getTheme()).isSameAs(theme);
        assertThat(result).isSameAs(saved);
    }

    @Test
    @DisplayName("create : EntityNotFoundException si l'utilisateur n'existe pas")
    void create_shouldThrow_whenUserNotFound() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setThemeId(10L);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.create(request, EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Utilisateur introuvable");

        verifyNoInteractions(themeRepository);
        verify(articleRepository, never()).save(any());
    }

    @Test
    @DisplayName("create : EntityNotFoundException si le thème n'existe pas")
    void create_shouldThrow_whenThemeNotFound() {
        CreateArticleRequest request = new CreateArticleRequest();
        request.setThemeId(99L);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(author));
        when(themeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> articleService.create(request, EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Thème introuvable");

        verify(articleRepository, never()).save(any());
    }
}