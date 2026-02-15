package kz.rsidash.service;

import jakarta.validation.constraints.NotNull;
import kz.rsidash.Constants;
import kz.rsidash.dto.post.PostDto;
import kz.rsidash.exception.EntityNotFoundException;
import kz.rsidash.mapper.post.PostMapper;
import kz.rsidash.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostRepository postRepository;

    private final PostMapper postMapper;

    @Transactional
    public PostDto likePost(
            final @NotNull(message = "Post is required") Long postId
    ) throws EntityNotFoundException {
        final var post = postRepository.likePost(postId, Constants.Numbers.INT_ONE);

        return postMapper.toDto(post);
    }

}
