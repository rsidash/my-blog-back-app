package kz.rsidash.repository;

import kz.rsidash.model.Comment;
import kz.rsidash.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
    List<Comment> getComments(Long postId);
    Optional<Comment> getComment(Long postId, Long commentId);
    Comment addComment(Comment comment);
    Comment updateComment(Long commentId, Comment comment);
    void deleteComment(Long postId, Long commentId);
}
