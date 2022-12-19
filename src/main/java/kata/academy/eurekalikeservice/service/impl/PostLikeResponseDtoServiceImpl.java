package kata.academy.eurekalikeservice.service.impl;

import kata.academy.eurekalikeservice.model.dto.PostLikeResponseDto;
import kata.academy.eurekalikeservice.repository.PostLikeRepository;
import kata.academy.eurekalikeservice.service.PostLikeResponseDtoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class PostLikeResponseDtoServiceImpl implements PostLikeResponseDtoService {

    private final PostLikeRepository postLikeRepository;

    public List<PostLikeResponseDto> getPostLikeResponseDtos(List<Long> postIds) {
        return postIds.stream()
                .map(postId -> new PostLikeResponseDto(postId,
                        postLikeRepository.countByPostIdAndPositive(postId, true),
                        postLikeRepository.countByPostIdAndPositive(postId, false))
                ).toList();
    }

}
