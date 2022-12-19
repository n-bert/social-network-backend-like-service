package kata.academy.eurekalikeservice.rest.inner;

import kata.academy.eurekalikeservice.model.dto.CommentLikeResponseDto;
import kata.academy.eurekalikeservice.service.CommentLikeResponseDtoService;
import kata.academy.eurekalikeservice.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/internal/v1/likes/comments")
public class CommentLikeInternalRestController {

    private final CommentLikeService commentLikeService;
    private final CommentLikeResponseDtoService commentLikeResponseDtoService;

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteByCommentId(@PathVariable @Positive Long commentId) {
        commentLikeService.deleteByCommentId(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    ResponseEntity<Void> deleteAllByCommentIds(@RequestBody List<Long> commentIds) {
        commentLikeService.deleteAllByCommentIds(commentIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    ResponseEntity<List<CommentLikeResponseDto>> getCommentLikeResponseDtos(@RequestParam List<Long> commentIds) {
        return ResponseEntity.ok(commentLikeResponseDtoService.getCommentLikeResponseDtos(commentIds));
    }
}
