package kata.academy.eurekalikeservice.model.dto;

public record CommentLikeResponseDto (
        Long CommentId,
        Integer positiveLikesCount,
        Integer negativeLikesCount) {
}
