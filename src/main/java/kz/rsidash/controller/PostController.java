package kz.rsidash.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kz.rsidash.Constants;
import kz.rsidash.dto.post.PostCreateRequest;
import kz.rsidash.dto.post.PostDto;
import kz.rsidash.dto.post.PostPageResponse;
import kz.rsidash.dto.post.PostUpdateRequest;
import kz.rsidash.exception.EntityNotFoundException;
import kz.rsidash.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping(Constants.POSTS_ROOT)
    public PostPageResponse getPosts(
            final @RequestParam(name = "search") @NotBlank String search,
            final @RequestParam(name = "pageNumber") int page,
            final @RequestParam(name = "pageSize") int size
    ) {
        return postService.getPosts(search, page, size);
    }

    @PostMapping(Constants.POST)
    public PostDto getPost(final @PathVariable(name = "postId") @NotNull Long postId) {
        return postService.getPost(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(Constants.POSTS_ROOT)
    @ResponseStatus(HttpStatus.OK)
    public PostDto addPost(final @RequestBody @Valid PostCreateRequest request) {
        return postService.addPost(request);
    }

    @PutMapping(Constants.POST)
    @ResponseStatus(HttpStatus.OK)
    public PostDto editPost(
            final @PathVariable(name = "postId") @NotNull Long postId,
            final @RequestBody @Valid PostUpdateRequest request
    ) {
        try {
            return postService.updatePost(postId, request);
        } catch (EntityNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @DeleteMapping(Constants.POST)
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(final @PathVariable(name = "postId") @NotNull Long postId) {
        try {
            postService.deletePost(postId);
        } catch (EntityNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

}