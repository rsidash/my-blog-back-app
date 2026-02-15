package kz.rsidash.repository.comment;

import kz.rsidash.Constants;
import kz.rsidash.exception.EntityNotFoundException;
import kz.rsidash.mapper.comment.CommentResultSetMapper;
import kz.rsidash.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcNativePostCommentRepository implements PostCommentRepository {

    private static final String COMMENT_NOT_FOUND_MSG_TEMPLATE = "Comment with id = [%d] not found";

    private final JdbcTemplate jdbcTemplate;
    private final CommentResultSetMapper commentResultSetMapper;

    @Override
    public List<Comment> getComments(Long postId) {
        return jdbcTemplate.query(
                "SELECT id, text, postId FROM comments WHERE postId = ?",
                (rs, rowNum) -> commentResultSetMapper.toEntity(rs),
                postId
        );
    }

    @Override
    public Optional<Comment> getComment(Long postId, Long commentId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT id, text, postId FROM comments WHERE id = ? AND postId = ?",
                    (rs, rowNum) -> commentResultSetMapper.toEntity(rs),
                    commentId, postId
            ));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Comment addComment(Comment comment) {
        final var keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO comments (text, postId) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, comment.getText());
            ps.setLong(2, comment.getPostId());
            return ps;
        }, keyHolder);

        comment.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return comment;
    }

    @Override
    public Comment updateComment(Long commentId, Comment comment) {
        final var updated = jdbcTemplate.update(
                "UPDATE comments SET text = ? WHERE id = ?",
                comment.getText(), commentId
        );

        if (updated == Constants.Numbers.INT_ZERO) {
            throw new EntityNotFoundException(String.format(COMMENT_NOT_FOUND_MSG_TEMPLATE, commentId));
        }

        return getComment(comment.getPostId(), commentId).orElseThrow();
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        final var updated = jdbcTemplate.update(
                "DELETE FROM comments WHERE id = ? AND postId = ?",
                commentId, postId
        );

        if (updated == Constants.Numbers.INT_ZERO) {
            throw new EntityNotFoundException(String.format(COMMENT_NOT_FOUND_MSG_TEMPLATE, commentId));
        }
    }

}
