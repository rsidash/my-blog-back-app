package kz.rsidash.service;

import kz.rsidash.dto.post.PostCreateRequest;
import kz.rsidash.dto.post.PostUpdateRequest;
import kz.rsidash.exception.EntityNotFoundException;
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
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Test
    void shouldCreatePost() {
        var request = new PostCreateRequest("Test Title", "Test Text", List.of("tag1", "tag2"));

        var result = postService.addPost(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(result.getText()).isEqualTo("Test Text");
        assertThat(result.getTags()).containsExactly("tag1", "tag2");
        assertThat(result.getLikesCount()).isZero();
        assertThat(result.getCommentsCount()).isZero();
    }

    @Test
    void shouldGetPost() {
        var created = postService.addPost(new PostCreateRequest("Title", "Text", List.of()));

        var result = postService.getPost(created.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(created.getId());
        assertThat(result.get().getTitle()).isEqualTo("Title");
    }

    @Test
    void shouldReturnEmptyWhenPostNotFound() {
        var result = postService.getPost(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldUpdatePost() {
        var created = postService.addPost(new PostCreateRequest("Old Title", "Old Text", List.of()));
        var updateRequest = new PostUpdateRequest(1L, "New Title", "New Text", List.of("new"));

        var result = postService.updatePost(created.getId(), updateRequest);

        assertThat(result.getId()).isEqualTo(created.getId());
        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getText()).isEqualTo("New Text");
        assertThat(result.getTags()).containsExactly("new");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentPost() {
        var updateRequest = new PostUpdateRequest(1L, "Title", "Text", List.of());

        assertThatThrownBy(() -> postService.updatePost(999L, updateRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeletePost() {
        var created = postService.addPost(new PostCreateRequest("Title", "Text", List.of()));

        postService.deletePost(created.getId());

        assertThat(postService.getPost(created.getId())).isEmpty();
    }

    @Test
    void shouldGetPostsWithPagination() {
        postService.addPost(new PostCreateRequest("First Post", "Content", List.of()));
        postService.addPost(new PostCreateRequest("Second Post", "Content", List.of()));

        var result = postService.getPosts("", 0, 10);

        assertThat(result.getPosts()).hasSize(2);
    }

    @Test
    void shouldSearchPostsByTitle() {
        postService.addPost(new PostCreateRequest("Java Tutorial", "Content", List.of()));
        postService.addPost(new PostCreateRequest("Python Guide", "Content", List.of()));

        var result = postService.getPosts("java", 0, 10);

        assertThat(result.getPosts()).hasSize(1);
        assertThat(result.getPosts().getFirst().getTitle()).isEqualTo("Java Tutorial");
    }
}
