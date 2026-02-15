package kz.rsidash.repository.comment;

import kz.rsidash.model.Comment;

import java.util.List;
import java.util.Optional;

public interface PostCommentRepository {
    List<Comment> getComments(Long postId);
    Optional<Comment> getComment(Long postId, Long commentId);
    Comment addComment(Comment comment);
    Comment updateComment(Long commentId, Comment comment);
    void deleteComment(Long postId, Long commentId);
}
