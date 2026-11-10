package com.mdd.services;

import com.mdd.models.Article;
import com.mdd.models.Comment;
import com.mdd.models.User;
import com.mdd.payload.request.CreateCommentRequest;
import com.mdd.repository.ArticleRepository;
import com.mdd.repository.CommentRepository;
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
class CommentServiceTest {

    private static final String EMAIL = "user@test.com";
    private static final Long ARTICLE_ID = 1L;

    @Mock private CommentRepository commentRepository;
    @Mock private ArticleRepository articleRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CommentService commentService;

    private User author;
    private Article article;

    @BeforeEach
    void setUp() {
        author = User.builder().id(1L).email(EMAIL).build();
        article = Article.builder().id(ARTICLE_ID).title("Titre").build();
    }

    // ---------- findByArticleId ----------

    @Test
    @DisplayName("findByArticleId : retourne les commentaires fournis par le repository")
    void findByArticleId_shouldReturnCommentsFromRepository() {
        Comment c1 = Comment.builder().id(1L).content("récent").build();
        Comment c2 = Comment.builder().id(2L).content("ancien").build();

        when(commentRepository.findByArticleIdOrderByCreatedAtDesc(ARTICLE_ID))
                .thenReturn(List.of(c1, c2));

        List<Comment> result = commentService.findByArticleId(ARTICLE_ID);

        assertThat(result).containsExactly(c1, c2);
    }

    @Test
    @DisplayName("findByArticleId : liste vide si aucun commentaire")
    void findByArticleId_shouldReturnEmptyList_whenNoComments() {
        when(commentRepository.findByArticleIdOrderByCreatedAtDesc(ARTICLE_ID))
                .thenReturn(List.of());

        assertThat(commentService.findByArticleId(ARTICLE_ID)).isEmpty();
    }

    // ---------- create ----------

    @Test
    @DisplayName("create : sauvegarde un commentaire avec le bon contenu, auteur et article")
    void create_shouldSaveCommentWithAuthorAndArticle() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Super article !");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(author));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(article));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

        Comment result = commentService.create(ARTICLE_ID, request, EMAIL);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment saved = captor.getValue();

        assertThat(saved.getContent()).isEqualTo("Super article !");
        assertThat(saved.getAuthor()).isSameAs(author);
        assertThat(saved.getArticle()).isSameAs(article);
        assertThat(result).isSameAs(saved);
    }

    @Test
    @DisplayName("create : EntityNotFoundException si l'utilisateur n'existe pas")
    void create_shouldThrow_whenUserNotFound() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Salut");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(ARTICLE_ID, request, EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Utilisateur introuvable");

        verifyNoInteractions(articleRepository);
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("create : EntityNotFoundException si l'article n'existe pas")
    void create_shouldThrow_whenArticleNotFound() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setContent("Salut");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(author));
        when(articleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(99L, request, EMAIL))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Article introuvable");

        verify(commentRepository, never()).save(any());
    }
}