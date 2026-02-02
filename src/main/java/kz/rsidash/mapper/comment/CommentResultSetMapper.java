package kz.rsidash.mapper.comment;

import kz.rsidash.model.Comment;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CommentResultSetMapper {

    public Comment toEntity(ResultSet rs) throws SQLException {
        return Comment.builder()
                .id(rs.getLong("id"))
                .text(rs.getString("text"))
                .postId(rs.getLong("postId"))
                .build();
    }

}
