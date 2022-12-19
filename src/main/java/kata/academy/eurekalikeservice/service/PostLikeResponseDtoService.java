package kata.academy.eurekalikeservice.service;

import kata.academy.eurekalikeservice.model.dto.PostLikeResponseDto;

import java.util.List;


public interface PostLikeResponseDtoService {

    List<PostLikeResponseDto> getPostLikeResponseDtos(List<Long> postIds);
}
