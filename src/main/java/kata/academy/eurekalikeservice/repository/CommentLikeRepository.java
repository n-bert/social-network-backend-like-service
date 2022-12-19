package kata.academy.eurekalikeservice.repository;

import kata.academy.eurekalikeservice.model.dto.CommentLikeResponseDto;
import kata.academy.eurekalikeservice.model.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    @Modifying
    @Query("""
            DELETE
            FROM CommentLike cl
            WHERE cl.commentId = :commentId
                                """)
    void deleteByCommentId(Long commentId);

    int countByCommentIdAndPositive(Long commentId, Boolean positive);

    @Query("""
            SELECT NEW kata.academy.eurekalikeservice.model.dto.CommentLikeResponseDto (
                cl.commentId,
                CAST(SUM(CASE WHEN cl.positive = true THEN 1 ELSE 0 END) AS integer),
                CAST(SUM(CASE WHEN cl.positive = false THEN 1 ELSE 0 END) AS integer))
            FROM CommentLike cl
            WHERE cl.commentId IN :commentIds
            GROUP BY cl.commentId
            ORDER BY cl.commentId
            """)
    List<CommentLikeResponseDto> countPositiveAndNegativeLikesByCommentIdIn(List<Long> commentIds);
}
