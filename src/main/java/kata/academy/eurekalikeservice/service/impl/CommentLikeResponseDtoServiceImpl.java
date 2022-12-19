package kata.academy.eurekalikeservice.service.impl;

import kata.academy.eurekalikeservice.model.dto.CommentLikeResponseDto;
import kata.academy.eurekalikeservice.repository.CommentLikeRepository;
import kata.academy.eurekalikeservice.service.CommentLikeResponseDtoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentLikeResponseDtoServiceImpl implements CommentLikeResponseDtoService {
    private final CommentLikeRepository commentLikeRepository;

    @Override
    public List<CommentLikeResponseDto> getCommentLikeResponseDtos(List<Long> commentIds) {
        return commentLikeRepository.countPositiveAndNegativeLikesByCommentIdIn(commentIds);
    }
}
