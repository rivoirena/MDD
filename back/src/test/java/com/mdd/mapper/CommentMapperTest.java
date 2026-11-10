package com.mdd.mapper;

import com.mdd.dto.CommentDto;
import com.mdd.models.Article;
import com.mdd.models.Comment;
import com.mdd.models.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toDto_mapsCommentAndAssociations() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 15, 11, 30);
        User author = User.builder()
                .id(2L)
                .username("alice")
                .build();
        Article article = Article.builder()
                .id(3L)
                .title("Mon article")
                .build();
        Comment comment = Comment.builder()
                .id(4L)
                .content("Super article !")
                .createdAt(createdAt)
                .author(author)
                .article(article)
                .build();

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertEquals(4L, result.getId());
        assertEquals("Super article !", result.getContent());
        assertEquals(2L, result.getAuthorId());
        assertEquals("alice", result.getAuthorName());
        assertEquals(3L, result.getArticleId());
        assertEquals(createdAt, result.getCreatedAt());
    }

    @Test
    void toDto_returnsNullWhenCommentIsNull() {
        assertNull(mapper.toDto((Comment) null));
    }

    @Test
    void toDto_handlesNullAssociations() {
        Comment comment = Comment.builder()
                .id(4L)
                .content("Commentaire")
                .build();

        CommentDto result = mapper.toDto(comment);

        assertNotNull(result);
        assertNull(result.getAuthorId());
        assertNull(result.getAuthorName());
        assertNull(result.getArticleId());
    }

    @Test
    void toDto_mapsListInOrderAndHandlesNullElements() {
        Comment first = Comment.builder().id(1L).content("Premier").build();
        Comment second = Comment.builder().id(2L).content("Deuxième").build();

        List<CommentDto> result = mapper.toDto(Arrays.asList(first, null, second));

        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertNull(result.get(1));
        assertEquals(2L, result.get(2).getId());
    }

    @Test
    void toDto_returnsNullWhenListIsNull() {
        assertNull(mapper.toDto((List<Comment>) null));
    }
}