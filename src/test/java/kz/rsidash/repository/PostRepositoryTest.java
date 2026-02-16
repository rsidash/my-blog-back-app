package kz.rsidash.repository;

import kz.rsidash.exception.EntityNotFoundException;
import kz.rsidash.model.Post;
import kz.rsidash.repository.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql("/schema.sql")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void shouldSavePost() {
        Post post = Post.builder()
                .title("Test Post")
                .text("Test Content")
                .tags(List.of("java", "spring"))
                .build();

        Post saved = postRepository.save(post);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Post");
        assertThat(saved.getLikesCount()).isZero();
        assertThat(saved.getCommentsCount()).isZero();
    }

    @Test
    void shouldFindPostById() {
        Post post = postRepository.save(Post.builder()
                .title("Post")
                .text("Content")
                .tags(List.of())
                .build());

        var found = postRepository.find(post.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Post");
    }

    @Test
    void shouldReturnEmptyWhenPostNotFound() {
        var found = postRepository.find(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllPosts() {
        postRepository.save(Post.builder().title("Java Post").text("Content").tags(List.of()).build());
        postRepository.save(Post.builder().title("Python Post").text("Content").tags(List.of()).build());

        var page = postRepository.findAll("", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldSearchPostsByTitle() {
        postRepository.save(Post.builder().title("Java Tutorial").text("Content").tags(List.of()).build());
        postRepository.save(Post.builder().title("Python Guide").text("Content").tags(List.of()).build());

        var page = postRepository.findAll("java", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getTitle()).isEqualTo("Java Tutorial");
    }

    @Test
    void shouldUpdatePost() {
        Post post = postRepository.save(Post.builder()
                .title("Old Title")
                .text("Old Text")
                .tags(List.of("old"))
                .build());

        Post updated = Post.builder()
                .title("New Title")
                .text("New Text")
                .tags(List.of("new"))
                .build();

        Post result = postRepository.update(post.getId(), updated);

        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getText()).isEqualTo("New Text");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentPost() {
        Post post = Post.builder().title("Title").text("Text").tags(List.of()).build();

        assertThatThrownBy(() -> postRepository.update(999L, post))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldDeletePost() {
        Post post = postRepository.save(Post.builder()
                .title("Post")
                .text("Content")
                .tags(List.of())
                .build());

        postRepository.delete(post.getId());

        assertThat(postRepository.find(post.getId())).isEmpty();
    }

    @Test
    void shouldLikePost() {
        Post post = postRepository.save(Post.builder()
                .title("Post")
                .text("Content")
                .tags(List.of())
                .build());

        Post liked = postRepository.likePost(post.getId(), 1);

        assertThat(liked.getLikesCount()).isEqualTo(1);
    }

    @Test
    void shouldUpdateImagePath() {
        Post post = postRepository.save(Post.builder()
                .title("Post")
                .text("Content")
                .tags(List.of())
                .build());

        postRepository.updateImagePath(post.getId(), "/images/test.jpg");

        var imagePath = postRepository.findImagePath(post.getId());
        assertThat(imagePath).isPresent();
        assertThat(imagePath.get()).isEqualTo("/images/test.jpg");
    }
}
