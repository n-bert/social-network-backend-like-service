package kata.academy.eurekalikeservice.model.dto;

public record PostLikeResponseDto(
        Long postId,
        Integer positiveLikesCount,
        Integer negativeLikesCount) {
}