package kz.rsidash.mapper.post;

import kz.rsidash.model.Post;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class PostResultSetMapper {

    public Post toEntity(ResultSet rs) throws SQLException {
        return Post.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .text(rs.getString("text"))
                .tags(Optional.ofNullable(rs.getString("tags"))
                        .map(s -> Arrays.stream(s.split(","))
                                .filter(t -> !t.isBlank())
                                .toList())
                        .orElse(List.of()))
                .likesCount(rs.getLong("likesCount"))
                .commentsCount(rs.getLong("commentsCount"))
                .build();
    }

}
