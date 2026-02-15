package kz.rsidash.service;

import jakarta.validation.constraints.NotNull;
import kz.rsidash.dto.comment.CommentDto;
import kz.rsidash.dto.comment.CommentUpdateRequest;
import kz.rsidash.exception.EntityNotFoundException;
import kz.rsidash.mapper.comment.CommentMapper;
import kz.rsidash.mapper.comment.CommentUpdateRequestMapper;
import kz.rsidash.repository.comment.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommentService {

    private final PostCommentRepository postCommentRepository;

    private final CommentMapper commentMapper;
    private final CommentUpdateRequestMapper commentUpdateRequestMapper;

    @Transactional(readOnly = true)
    public List<CommentDto> getPostComments(final @NotNull Long postId) {
        return postCommentRepository.getComments(postId).stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<CommentDto> getPostComment(final @NotNull Long postId, final @NotNull Long commentId) {
        return postCommentRepository.getComment(postId, commentId)
                .map(commentMapper::toDto);
    }

    @Transactional
    public CommentDto addComment(
            final @NotNull Long postId,
            final @NotNull CommentUpdateRequest request
    ) throws EntityNotFoundException {
        final var comment = commentUpdateRequestMapper.toEntity(request, postId);
        final var saved = postCommentRepository.addComment(comment);

        return commentMapper.toDto(saved);
    }

    @Transactional
    public CommentDto updateComment(
            final @NotNull Long postId,
            final @NotNull Long commentId,
            final @NotNull CommentUpdateRequest request
    ) throws EntityNotFoundException {
        final var comment = commentUpdateRequestMapper.toEntity(request, postId);

        final var result = postCommentRepository.updateComment(commentId, comment);

        return commentMapper.toDto(result);
    }

    @Transactional
    public void deleteComment(
            final @NotNull Long postId,
            final @NotNull Long commentId
    ) throws EntityNotFoundException {
        postCommentRepository.deleteComment(postId, commentId);
    }

}
