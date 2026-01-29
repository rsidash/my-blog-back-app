package kz.rsidash.controller;

import jakarta.validation.constraints.NotNull;
import kz.rsidash.Constants;
import kz.rsidash.dto.post.PostDto;
import kz.rsidash.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PostMapping(Constants.POST_ADD_LIKE)
    @ResponseStatus(HttpStatus.OK)
    public PostDto likePost(final @PathVariable(name = "postId") @NotNull Long postId) {
        return postLikeService.likePost(postId);
    }

}
