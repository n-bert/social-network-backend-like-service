package kata.academy.eurekalikeservice.outer;

import kata.academy.eurekalikeservice.SpringSimpleContextTest;
import kata.academy.eurekalikeservice.feign.ContentServiceFeignClient;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBeans({
        @MockBean(ContentServiceFeignClient.class)
})
@DirtiesContext
public class PostLikeRestControllerIT extends SpringSimpleContextTest {

    @Autowired
    private ContentServiceFeignClient contentServiceFeignClient;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/PostLikeRestController/addPostLike_SuccessfulTest/After.sql")
    public void addPostLike_SuccessfulTest() throws Exception {
        Long postId = 1L;
        Long userId = 1L;
        boolean positive = true;
        doReturn(Boolean.TRUE).when(contentServiceFeignClient).existsByPostId(postId);
        mockMvc.perform(post("/api/v1/likes/posts/{postId}", postId)
                        .header("userId", userId.toString())
                        .param("positive", String.valueOf(positive))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertTrue(entityManager.createQuery(
                        """
                                SELECT COUNT(pl.id) > 0
                                FROM PostLike pl
                                WHERE pl.postId = :postId
                                AND pl.userId = :userId
                                AND pl.positive = :positive
                                """, Boolean.class)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .setParameter("positive", positive)
                .getSingleResult());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/PostLikeRestController/addPostLike_PostLikeExistsTest/Before.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/PostLikeRestController/addPostLike_PostLikeExistsTest/After.sql")
    public void addPostLike_PostLikeExistsTest() throws Exception {
        Long postId = 1L;
        Long userId = 1L;
        boolean positive = true;
        doReturn(Boolean.TRUE).when(contentServiceFeignClient).existsByPostId(postId);
        mockMvc.perform(post("/api/v1/likes/posts/{postId}", postId)
                        .header("userId", userId.toString())
                        .param("positive", String.valueOf(positive))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Is.is(
                        String.format("Пользователь userId %d уже лайкнул пост postId %d",
                                postId, userId)
                )));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/PostLikeRestController/updatePostLike_SuccessfulTest/Before.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/PostLikeRestController/updatePostLike_SuccessfulTest/After.sql")
    public void updatePostLike_SuccessfulTest() throws Exception {
        Long postId = 1L;
        long userId = 1L;
        boolean positive = true;
        doReturn(Boolean.TRUE).when(contentServiceFeignClient).existsByPostId(postId);
        mockMvc.perform(put("/api/v1/likes/posts/{postId}", postId)
                        .header("userId", Long.toString(userId))
                        .param("positive", String.valueOf(positive))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertTrue(entityManager.createQuery("""
                        SELECT COUNT(pl.id) > 0
                        FROM PostLike pl
                        WHERE pl.postId = :postId
                        AND pl.userId = :userId
                        AND pl.positive = :positive
                        """, Boolean.class)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .setParameter("positive", positive)
                .getSingleResult());
    }

    @Test
    public void updatePostLike_FailTest() throws Exception {
        Long postId = 1L;
        long userId = 1L;
        boolean positive = true;
        doReturn(Boolean.TRUE).when(contentServiceFeignClient).existsByPostId(postId);
        mockMvc.perform(put("/api/v1/likes/posts/{postId}", postId)
                        .header("userId", Long.toString(userId))
                        .param("positive", String.valueOf(positive))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Is.is(
                        String.format("Лайк с postId %d, userId %d нет в базе данных", postId, userId)
                )));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/PostLikeRestController/deletePostLike_SuccessfulTest/Before.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/PostLikeRestController/deletePostLike_SuccessfulTest/After.sql")
    public void deletePostLike_SuccessfulTest() throws Exception {
        Long postId = 1L;
        long userId = 1L;
        doReturn(Boolean.TRUE).when(contentServiceFeignClient).existsByPostId(postId);
        mockMvc.perform(delete("/api/v1/likes/posts/{postId}", postId)
                        .header("userId", Long.toString(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertFalse(entityManager.createQuery("""
                        SELECT COUNT(pl.id) > 0
                        FROM PostLike pl
                        WHERE pl.postId = :postId
                        AND pl.userId = :userId
                        """, Boolean.class)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getSingleResult());
    }

    @Test
    public void deletePostLike_FailTest() throws Exception {
        long postId = 1L;
        long userId = 1L;
        doReturn(Boolean.TRUE).when(contentServiceFeignClient).existsByPostId(postId);
        mockMvc.perform(delete("/api/v1/likes/posts/{postId}", postId)
                        .header("userId", Long.toString(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text", Is.is(
                        String.format("Лайк с postId %d, userId %d нет в базе данных", postId, userId)
                )));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/PostLikeRestController/getPostLikeCount_SuccessfulTest/Before.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/PostLikeRestController/getPostLikeCount_SuccessfulTest/After.sql")
    public void getPostLikeCount_SuccessfulTest() throws Exception {
        long postId = 1L;
        String positiveState = String.valueOf(true);
        int countOfPositiveLikes = 1;
        doReturn(Boolean.TRUE).when(contentServiceFeignClient).existsByPostId(postId);
        mockMvc.perform(get("/api/v1/likes/posts/{postId}/count", postId)
                        .param("positive", positiveState)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data", Is.is(countOfPositiveLikes)));
    }

}
