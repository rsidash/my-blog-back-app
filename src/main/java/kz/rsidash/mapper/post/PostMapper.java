package kz.rsidash.mapper.post;

import jakarta.validation.constraints.NotNull;
import kz.rsidash.dto.post.PostDto;
import kz.rsidash.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {

    public PostDto toDto(@NotNull Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .text(post.getText())
                .tags(post.getTags())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .build();
    }

    public Post toEntity(@NotNull PostDto post) {
        return Post.builder()
                .id(post.getId())
                .title(post.getTitle())
                .text(post.getText())
                .tags(post.getTags())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .build();
    }

}
