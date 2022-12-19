package kata.academy.eurekalikeservice.inner;

import com.fasterxml.jackson.databind.ObjectMapper;
import kata.academy.eurekalikeservice.SpringSimpleContextTest;
import kata.academy.eurekalikeservice.feign.ContentServiceFeignClient;
import kata.academy.eurekalikeservice.model.dto.PostLikeResponseDto;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@MockBeans({
        @MockBean(ContentServiceFeignClient.class)
})
@DirtiesContext
public class PostLikeInternalRestControllerIT extends SpringSimpleContextTest {

    @Autowired
    private ContentServiceFeignClient contentServiceFeignClient;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/inner/PostLikeInternalRestController/deleteByPostId_SuccessfulTest/Before.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/inner/PostLikeInternalRestController/deleteByPostId_SuccessfulTest/After.sql")
    public void deleteByPostId_SuccessfulTest() throws Exception {
        long postId = 1L;
        doReturn(Boolean.TRUE).when(contentServiceFeignClient).existsByPostId(postId);
        mockMvc.perform(delete("/api/internal/v1/likes/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertFalse(entityManager.createQuery("""
                        SELECT COUNT(pl.id) > 0
                        FROM PostLike pl
                        WHERE pl.postId = :postId
                        """, Boolean.class)
                .setParameter("postId", postId)
                .getSingleResult());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/inner/PostLikeInternalRestController/getTopPostIdsByCount_CorrectTest/Before.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/inner/PostLikeInternalRestController/getTopPostIdsByCount_CorrectTest/After.sql")
    public void getTopPostIdsByCount_CorrectTest() throws Exception {
        List<Integer> expectedList = List.of(1, 2);
        int count = 3;
        mockMvc.perform(get(URI.create("/api/internal/v1/likes/posts/top"))
                        .param("count", String.valueOf(count))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Is.is(expectedList)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/inner/PostLikeInternalRestController/getPostLikeResponseDtos_Test/Before.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/inner/PostLikeInternalRestController/getPostLikeResponseDtos_Test/After.sql")
    public void getPostLikeResponseDtos_Test() throws Exception {
        PostLikeResponseDto[] expectedList = {
                new PostLikeResponseDto(1L, 2, 1),
                new PostLikeResponseDto(2L, 2, 0)
        };
        mockMvc.perform(get(URI.create("/api/internal/v1/likes/posts"))
                        .param("postIds", "1", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(new ObjectMapper().writeValueAsString(expectedList)));
    }
}
