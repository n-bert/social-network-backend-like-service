package kata.academy.eurekalikeservice.service;

import kata.academy.eurekalikeservice.model.dto.CommentLikeResponseDto;

import java.util.List;

public interface CommentLikeResponseDtoService {
    List<CommentLikeResponseDto> getCommentLikeResponseDtos(List<Long> commentIds);
}
