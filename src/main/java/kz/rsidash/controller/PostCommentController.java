package kz.rsidash.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kz.rsidash.Constants;
import kz.rsidash.dto.comment.CommentDto;
import kz.rsidash.dto.comment.CommentUpdateRequest;
import kz.rsidash.exception.EntityNotFoundException;
import kz.rsidash.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostCommentController {

    private final PostCommentService postCommentService;

    @GetMapping(Constants.POST_COMMENTS)
    public List<CommentDto> getPostComments(final @PathVariable(name = "postId") @NotNull Long postId) {
        return postCommentService.getPostComments(postId);
    }

    @GetMapping(Constants.POST_COMMENT)
    public CommentDto getPostComment(
            final @PathVariable(name = "postId") @NotNull Long postId,
            final @PathVariable(name = "commentId") @NotNull Long commentId
    ) {
        return postCommentService.getPostComment(postId, commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(Constants.POST_COMMENTS)
    @ResponseStatus(HttpStatus.OK)
    public CommentDto commentPost(
            final @PathVariable(name = "postId") @NotNull Long postId,
            final @RequestBody @Valid CommentUpdateRequest request
    ) {
        return postCommentService.addComment(postId, request);
    }

    @PutMapping(Constants.POST_COMMENT)
    @ResponseStatus(HttpStatus.OK)
    public CommentDto editPostComment(
            final @PathVariable(name = "postId") @NotNull Long postId,
            final @PathVariable(name = "commentId") @NotNull Long commentId,
            final @RequestBody @Valid CommentUpdateRequest request
    ) {
        return postCommentService.updateComment(postId, commentId, request);
    }

    @DeleteMapping(Constants.POST_COMMENT)
    @ResponseStatus(HttpStatus.OK)
    public void deletePostComment(
            final @PathVariable(name = "postId") @NotNull Long postId,
            final @PathVariable(name = "commentId") @NotNull Long commentId
    ) {
        postCommentService.deleteComment(postId, commentId);
    }

}
