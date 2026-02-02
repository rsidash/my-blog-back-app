package kz.rsidash.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kz.rsidash.Constants;
import kz.rsidash.dto.comment.CommentDto;
import kz.rsidash.dto.comment.CommentUpdateRequest;
import kz.rsidash.dto.post.PostCreateRequest;
import kz.rsidash.dto.post.PostDto;
import kz.rsidash.dto.post.PostPageResponse;
import kz.rsidash.dto.post.PostUpdateRequest;
import kz.rsidash.exception.EntityNotFoundException;
import kz.rsidash.mapper.comment.CommentMapper;
import kz.rsidash.mapper.comment.CommentUpdateRequestMapper;
import kz.rsidash.mapper.post.PostCreateRequestMapper;
import kz.rsidash.mapper.post.PostMapper;
import kz.rsidash.mapper.post.PostPageMapper;
import kz.rsidash.mapper.post.PostUpdateRequestMapper;
import kz.rsidash.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final PostMapper postMapper;
    private final PostPageMapper postPageMapper;
    private final PostCreateRequestMapper postCreateRequestMapper;
    private final PostUpdateRequestMapper postUpdateRequestMapper;

    @Transactional(readOnly = true)
    public PostPageResponse getPosts(
            final @NotBlank(message = "Search is required") String search,
            final int page, final int size
    ) {
        final var posts = postRepository.findAll(search, PageRequest.of(page, size));

        return postPageMapper.toDto(posts);
    }

    @Transactional(readOnly = true)
    public Optional<PostDto> getPost(final @NotNull(message = "Post id is required") Long postId) {
        return postRepository.find(postId)
                .map(postMapper::toDto);
    }

    @Transactional
    public PostDto addPost(final @NotNull(message = "Post is required") PostCreateRequest request) {
        final var post = postRepository.save(postCreateRequestMapper.toEntity(request));

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto updatePost(
            final @NotNull Long postId,
            final @NotNull(message = "Post is required") PostUpdateRequest request
    ) throws EntityNotFoundException {
        final var post = postRepository.update(postId, postUpdateRequestMapper.toEntity(request));

        return postMapper.toDto(post);
    }

    @Transactional
    public void deletePost(final @NotNull(message = "Post is required") Long postId) {
        postRepository.delete(postId);
    }

}
