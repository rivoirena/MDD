package com.mdd.mapper;

import com.mdd.dto.ArticleDto;
import com.mdd.models.Article;
import com.mdd.models.Theme;
import com.mdd.models.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArticleMapperTest {

    private final ArticleMapper mapper = Mappers.getMapper(ArticleMapper.class);

    @Test
    void toDto_mapsArticleAndAssociations() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 15, 10, 30);
        User author = User.builder()
                .id(2L)
                .username("alice")
                .build();
        Theme theme = Theme.builder()
                .id(3L)
                .title("Java")
                .build();
        Article article = Article.builder()
                .id(1L)
                .title("Mon article")
                .content("Contenu")
                .createdAt(createdAt)
                .author(author)
                .theme(theme)
                .build();

        ArticleDto result = mapper.toDto(article);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Mon article", result.getTitle());
        assertEquals("Contenu", result.getContent());
        assertEquals(2L, result.getAuthorId());
        assertEquals("alice", result.getAuthorName());
        assertEquals(3L, result.getThemeId());
        assertEquals("Java", result.getThemeName());
        assertEquals(createdAt, result.getCreatedAt());
    }

    @Test
    void toDto_returnsNullWhenArticleIsNull() {
        assertNull(mapper.toDto((Article) null));
    }

    @Test
    void toDto_handlesNullAssociations() {
        Article article = Article.builder()
                .id(1L)
                .title("Mon article")
                .content("Contenu")
                .build();

        ArticleDto result = mapper.toDto(article);

        assertNotNull(result);
        assertNull(result.getAuthorId());
        assertNull(result.getAuthorName());
        assertNull(result.getThemeId());
        assertNull(result.getThemeName());
    }

    @Test
    void toDto_mapsListInOrderAndHandlesNullElements() {
        Article first = Article.builder().id(1L).title("Premier").build();
        Article second = Article.builder().id(2L).title("Deuxième").build();

        List<ArticleDto> result = mapper.toDto(Arrays.asList(first, null, second));

        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertNull(result.get(1));
        assertEquals(2L, result.get(2).getId());
    }

    @Test
    void toDto_returnsNullWhenListIsNull() {
        assertNull(mapper.toDto((List<Article>) null));
    }
}