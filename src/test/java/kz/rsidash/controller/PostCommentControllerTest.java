package kz.rsidash.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.rsidash.config.TestConfig;
import kz.rsidash.dto.comment.CommentDto;
import kz.rsidash.dto.comment.CommentUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
class PostCommentControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        objectMapper = new ObjectMapper();
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        jdbcTemplate.execute("ALTER TABLE posts ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE comments ALTER COLUMN id RESTART WITH 1");
    }

    @Test
    void shouldAddComment() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post", "Content", "tag", 0, 0);

        CommentUpdateRequest request = CommentUpdateRequest.builder()
                .id(1L)
                .text("Great post!")
                .postId(1L)
                .build();

        String response = mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommentDto result = objectMapper.readValue(response, CommentDto.class);
        assertThat(result.getText()).isEqualTo("Great post!");
        assertThat(result.getPostId()).isEqualTo(1);
    }

    @Test
    void shouldGetComments() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post", "Content", "tag", 0, 0);
        jdbcTemplate.update("INSERT INTO comments (text, postId) VALUES (?, ?)", "Comment 1", 1);
        jdbcTemplate.update("INSERT INTO comments (text, postId) VALUES (?, ?)", "Comment 2", 1);

        String response = mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommentDto[] result = objectMapper.readValue(response, CommentDto[].class);
        assertThat(result).hasSize(2);
        assertThat(result[0].getText()).isEqualTo("Comment 1");
        assertThat(result[1].getText()).isEqualTo("Comment 2");
    }

    @Test
    void shouldGetComment() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post", "Content", "tag", 0, 0);
        jdbcTemplate.update("INSERT INTO comments (text, postId) VALUES (?, ?)", "Comment", 1);

        String response = mockMvc.perform(get("/api/posts/1/comments/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommentDto result = objectMapper.readValue(response, CommentDto.class);
        assertThat(result.getText()).isEqualTo("Comment");
    }

    @Test
    void shouldUpdateComment() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post", "Content", "tag", 0, 0);
        jdbcTemplate.update("INSERT INTO comments (text, postId) VALUES (?, ?)", "Old Comment", 1);

        CommentUpdateRequest request = CommentUpdateRequest.builder()
                .id(1L)
                .text("Updated Comment")
                .postId(1L)
                .build();

        String response = mockMvc.perform(put("/api/posts/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommentDto result = objectMapper.readValue(response, CommentDto.class);
        assertThat(result.getText()).isEqualTo("Updated Comment");
    }

    @Test
    void shouldDeleteComment() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post", "Content", "tag", 0, 0);
        jdbcTemplate.update("INSERT INTO comments (text, postId) VALUES (?, ?)", "Comment", 1);

        mockMvc.perform(delete("/api/posts/1/comments/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenCommentNotFound() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post", "Content", "tag", 0, 0);

        mockMvc.perform(get("/api/posts/1/comments/999"))
                .andExpect(status().isNotFound());
    }
}
