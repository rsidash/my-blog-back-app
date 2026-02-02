package kz.rsidash.mapper.post;

import jakarta.validation.constraints.NotNull;
import kz.rsidash.dto.post.PostUpdateRequest;
import kz.rsidash.model.Post;
import org.springframework.stereotype.Component;

@Component
public class PostUpdateRequestMapper {

    public Post toEntity(@NotNull PostUpdateRequest request) {
        return Post.builder()
                .title(request.getTitle())
                .text(request.getText())
                .tags(request.getTags())
                .build();
    }

}
