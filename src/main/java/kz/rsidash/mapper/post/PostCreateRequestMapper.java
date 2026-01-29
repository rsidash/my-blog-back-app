package kz.rsidash.mapper.post;

import jakarta.validation.constraints.NotNull;
import kz.rsidash.dto.post.PostCreateRequest;
import kz.rsidash.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostCreateRequestMapper {

    public Post toEntity(@NotNull PostCreateRequest request) {
        return Post.builder()
                .title(request.getTitle())
                .text(request.getText())
                .tags(request.getTags())
                .build();
    }

}
