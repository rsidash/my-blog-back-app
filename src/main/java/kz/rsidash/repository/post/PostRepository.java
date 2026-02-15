package kz.rsidash.repository.post;

import kz.rsidash.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PostRepository {
    Page<Post> findAll(String search, Pageable pageable);
    Optional<Post> find(Long id);
    Post save(Post post);
    Post update(Long id, Post post);
    void delete(Long id);
    Post likePost(Long id, int increment);
    Optional<String> findImagePath(Long postId);
    void updateImagePath(Long postId, String imagePath);
}
