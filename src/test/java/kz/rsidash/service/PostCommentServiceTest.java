package kz.rsidash.service;

import kz.rsidash.dto.comment.CommentUpdateRequest;
import kz.rsidash.dto.post.PostCreateRequest;
import kz.rsidash.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql("/schema.sql")
class PostCommentServiceTest {

    @Autowired
    private PostCommentService commentService;

    @Autowired
    private PostService postService;

    private Long postId;

    @BeforeEach
    void setUp() {
        var post = postService.addPost(new PostCreateRequest("Test Post", "Content", List.of()));
        postId = post.getId();
    }

    @Test
    void shouldAddComment() {
        var request = new CommentUpdateRequest(1L, "Great post!", 1L);

        var result = commentService.addComment(postId, request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getText()).isEqualTo("Great post!");
        assertThat(result.getPostId()).isEqualTo(postId);
    }

    @Test
    void shouldGetPostComments() {
        commentService.addComment(postId, new CommentUpdateRequest(1L, "Comment 1", 1L));
        commentService.addComment(postId, new CommentUpdateRequest(1L, "Comment 2", 1L));

        var result = commentService.getPostComments(postId);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("text").containsExactlyInAnyOrder("Comment 1", "Comment 2");
    }

    @Test
    void shouldGetSingleComment() {
        var created = commentService.addComment(postId, new CommentUpdateRequest(1L, "Test Comment", 1L));

        var result = commentService.getPostComment(postId, created.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(created.getId());
        assertThat(result.get().getText()).isEqualTo("Test Comment");
    }

    @Test
    void shouldReturnEmptyWhenCommentNotFound() {
        var result = commentService.getPostComment(postId, 999L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdateComment() {
        var created = commentService.addComment(postId, new CommentUpdateRequest(1L, "Old Text", 1L));
        var updateRequest = new CommentUpdateRequest(1L, "Updated Text", 1L);

        var result = commentService.updateComment(postId, created.getId(), updateRequest);

        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getText()).isEqualTo("Updated Text");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentComment() {
        var updateRequest = new CommentUpdateRequest(1L, "Text", 1L);

        assertThatThrownBy(() -> commentService.updateComment(postId, 999L, updateRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeleteComment() {
        var created = commentService.addComment(postId, new CommentUpdateRequest(1L, "Comment", 1L));

        commentService.deleteComment(postId, created.getId());

        assertThat(commentService.getPostComment(postId, created.getId())).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentComment() {
        assertThatThrownBy(() -> commentService.deleteComment(postId, 999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
