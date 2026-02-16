package kz.rsidash.repository;

import kz.rsidash.exception.EntityNotFoundException;
import kz.rsidash.model.Comment;
import kz.rsidash.model.Post;
import kz.rsidash.repository.comment.PostCommentRepository;
import kz.rsidash.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql("/schema.sql")
class PostCommentRepositoryTest {

    @Autowired
    private PostCommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long postId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1");
        Post post = postRepository.save(Post.builder()
                .title("Test Post")
                .text("Content")
                .tags(List.of())
                .build());
        postId = post.getId();
    }

    @Test
    void shouldAddComment() {
        Comment comment = Comment.builder()
                .text("Great post!")
                .postId(postId)
                .build();

        Comment saved = commentRepository.addComment(comment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getText()).isEqualTo("Great post!");
        assertThat(saved.getPostId()).isEqualTo(postId);
    }

    @Test
    void shouldGetComments() {
        commentRepository.addComment(Comment.builder().text("Comment 1").postId(postId).build());
        commentRepository.addComment(Comment.builder().text("Comment 2").postId(postId).build());

        List<Comment> comments = commentRepository.getComments(postId);

        assertThat(comments).hasSize(2);
        assertThat(comments).extracting(Comment::getText).containsExactlyInAnyOrder("Comment 1", "Comment 2");
    }

    @Test
    void shouldGetComment() {
        Comment comment = commentRepository.addComment(
                Comment.builder().text("Test Comment").postId(postId).build()
        );

        var found = commentRepository.getComment(postId, comment.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getText()).isEqualTo("Test Comment");
    }

    @Test
    void shouldReturnEmptyWhenCommentNotFound() {
        var found = commentRepository.getComment(postId, 999L);

        assertThat(found).isEmpty();
    }

    @Test
    void shouldUpdateComment() {
        Comment comment = commentRepository.addComment(
                Comment.builder().text("Old Text").postId(postId).build()
        );

        Comment updated = Comment.builder().text("Updated Text").postId(postId).build();
        Comment result = commentRepository.updateComment(comment.getId(), updated);

        assertThat(result.getText()).isEqualTo("Updated Text");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentComment() {
        Comment comment = Comment.builder().text("Text").postId(postId).build();

        assertThatThrownBy(() -> commentRepository.updateComment(999L, comment))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeleteComment() {
        Comment comment = commentRepository.addComment(
                Comment.builder().text("Comment").postId(postId).build()
        );

        commentRepository.deleteComment(postId, comment.getId());

        assertThat(commentRepository.getComment(postId, comment.getId())).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentComment() {
        assertThatThrownBy(() -> commentRepository.deleteComment(postId, 999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
