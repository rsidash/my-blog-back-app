package kz.rsidash.mapper.post;

import jakarta.validation.constraints.NotEmpty;
import kz.rsidash.dto.post.PostPageResponse;
import kz.rsidash.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostPageMapper {
    private final PostMapper postMapper;

    public PostPageResponse toDto(@NotEmpty Page<Post> posts) {
        return PostPageResponse.builder()
                .posts(posts.map(postMapper::toDto).getContent())
                .hasNext(posts.hasNext())
                .hasPrev(posts.hasPrevious())
                .lastPage(posts.getTotalPages())
                .build();
    }

}
