package kz.rsidash.mapper.comment;

import kz.rsidash.dto.comment.CommentUpdateRequest;
import kz.rsidash.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentUpdateRequestMapper {
    public Comment toEntity(CommentUpdateRequest request, Long postId) {
        return Comment.builder()
                .text(request.getText())
                .postId(postId)
                .build();
    }
}
