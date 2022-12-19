package kata.academy.eurekalikeservice.inner;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import kata.academy.eurekalikeservice.SpringSimpleContextTest;
import kata.academy.eurekalikeservice.model.dto.CommentLikeResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
public class CommentLikeInternalRestControllerIT extends SpringSimpleContextTest {

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/inner/CommentLikeInternalRestController/getCommentLikeResponseDtos_SuccessfulTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/inner/CommentLikeInternalRestController/getCommentLikeResponseDtos_SuccessfulTest/AfterTest.sql")
    public void getCommentLikeResponseDtos_SuccessTest() throws Exception {
        List<Long> commentIds = List.of(1L, 2L, 3L);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("commentIds", Lists.transform(commentIds, Object::toString));

        List<CommentLikeResponseDto> expectedList = entityManager.createQuery("""
            SELECT new kata.academy.eurekalikeservice.model.dto.CommentLikeResponseDto(
                cl.commentId,
                CAST(SUM(CASE WHEN cl.positive = true THEN 1 ELSE 0 END) AS integer),
                CAST(SUM(CASE WHEN cl.positive = false THEN 1 ELSE 0 END) AS integer))
            FROM CommentLike cl
            WHERE cl.commentId IN :commentIds
            GROUP BY cl.commentId
            ORDER BY cl.commentId
            """, CommentLikeResponseDto.class)
            .setParameter("commentIds", commentIds)
            .getResultList();

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mockMvc.perform(get("/api/internal/v1/likes/comments")
            .params(params)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(expectedList)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/inner/CommentLikeInternalRestController/deleteByCommentId_SuccessfulTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/inner/CommentLikeInternalRestController/deleteByCommentId_SuccessfulTest/AfterTest.sql")
    public void deleteByCommentId_SuccessfulTest() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(delete("/api/internal/v1/likes/comments/{commentId}", commentId))
            .andExpect(status().isOk());
        assertFalse(entityManager.createQuery("""
                                                SELECT COUNT(cl.id) > 0
                                                FROM CommentLike cl
                                                WHERE cl.commentId = :commentId
                                                """, Boolean.class)
            .setParameter("commentId", commentId)
            .getSingleResult());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/inner/CommentLikeInternalRestController/deleteAllByCommentIds_SuccessfulTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/inner/CommentLikeInternalRestController/deleteAllByCommentIds_SuccessfulTest/AfterTest.sql")
    public void deleteAllByCommentIds_SuccessfulTest() throws Exception {
        List<Long> commentIds = List.of(1L, 2L);

        mockMvc.perform(delete("/api/internal/v1/likes/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentIds)))
            .andExpect(status().isOk());
        assertFalse(entityManager.createQuery("""
                SELECT COUNT(cl.id) > 0
                FROM CommentLike cl
                WHERE cl.commentId IN :commentId
                """, Boolean.class)
            .setParameter("commentId", commentIds)
            .getSingleResult());
    }
}
