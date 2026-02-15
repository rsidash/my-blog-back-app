package kz.rsidash.repository.post;

import kz.rsidash.Constants;
import kz.rsidash.exception.EntityNotFoundException;
import kz.rsidash.mapper.post.PostResultSetMapper;
import kz.rsidash.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcNativePostRepository implements PostRepository {

    private static final String POST_NOT_FOUND_MSG_TEMPLATE = "Post with id = [%d] not found";

    private final JdbcTemplate jdbcTemplate;
    private final PostResultSetMapper postResultSetMapper;

    @Override
    public Page<Post> findAll(String search, Pageable pageable) {
        final var searchPattern = "%" + search.toLowerCase() + "%";

        final var where = """
                WHERE LOWER(title) LIKE ? OR LOWER(text) LIKE ?
                """;

        final var query = """
                SELECT id, title, text, tags, likesCount, commentsCount
                FROM posts
                """ + where + """
                LIMIT ? OFFSET ?
                """;

        final var posts = jdbcTemplate.query(
                query,
                (rs, rowNum) -> postResultSetMapper.toEntity(rs),
                searchPattern, searchPattern,
                pageable.getPageSize(),
                pageable.getOffset()
        );

        final var total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts " + where,
                Long.class,
                searchPattern, searchPattern
        );

        return new PageImpl<>(posts, pageable, Objects.requireNonNullElse(total, Constants.Numbers.LONG_ZERO));
    }

    @Override
    public Optional<Post> find(Long id) {
        final var query = """
                SELECT id, title, text, tags, likesCount, commentsCount
                FROM posts
                WHERE id = ?
                """;

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(
                            query, (rs, rowNum) -> postResultSetMapper.toEntity(rs), id
                    )
            );

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Post save(Post post) {
        final var query = """
                INSERT INTO posts (title, text, tags, likesCount, commentsCount)
                VALUES (?, ?, ?, ?, ?)
                """;

        final var keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getText());
            ps.setString(3, String.join(",", post.getTags()));
            ps.setLong(4, Constants.Numbers.LONG_ZERO);
            ps.setLong(5, Constants.Numbers.LONG_ZERO);
            return ps;
        }, keyHolder);

        post.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        post.setLikesCount(Constants.Numbers.LONG_ZERO);
        post.setCommentsCount(Constants.Numbers.LONG_ZERO);
        return post;
    }

    @Override
    public Post update(Long id, Post post) {
        final var query = """
                UPDATE posts
                SET title = ?, text = ?, tags = ?
                WHERE id = ?
                """;

        int updated = jdbcTemplate.update(
                query,
                post.getTitle(),
                post.getText(),
                String.join(",", post.getTags()),
                id
        );

        if (updated == Constants.Numbers.INT_ZERO) {
            throw new EntityNotFoundException(String.format(POST_NOT_FOUND_MSG_TEMPLATE, id));
        }

        return find(id).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        final var updated = jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);

        if (updated == Constants.Numbers.INT_ZERO) {
            throw new EntityNotFoundException(String.format(POST_NOT_FOUND_MSG_TEMPLATE, id));
        }
    }

    @Override
    public Post likePost(Long id, int increment) {
        final var updated = jdbcTemplate.update(
                "UPDATE posts SET likesCount = likesCount + ? WHERE id = ?",
                increment, id
        );

        if (updated == Constants.Numbers.INT_ZERO) {
            throw new EntityNotFoundException(String.format(POST_NOT_FOUND_MSG_TEMPLATE, id));
        }

        return find(id).orElseThrow();
    }

    @Override
    public void updateImagePath(Long id, String imageUrl) {
        final var updated = jdbcTemplate.update("UPDATE posts SET imagePath = ? WHERE id = ?", imageUrl, id);

        if (updated == Constants.Numbers.INT_ZERO) {
            throw new EntityNotFoundException(String.format(POST_NOT_FOUND_MSG_TEMPLATE, id));
        }
    }

    @Override
    public Optional<String> findImagePath(Long id) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    "SELECT imagePath FROM posts WHERE id = ?",
                    String.class,
                    id
            ));
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

}

