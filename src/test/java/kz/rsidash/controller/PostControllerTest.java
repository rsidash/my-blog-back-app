package kz.rsidash.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.rsidash.config.TestConfig;
import kz.rsidash.dto.post.PostCreateRequest;
import kz.rsidash.dto.post.PostDto;
import kz.rsidash.dto.post.PostPageResponse;
import kz.rsidash.dto.post.PostUpdateRequest;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
class PostControllerTest {

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
    void shouldCreatePost() throws Exception {
        PostCreateRequest request = PostCreateRequest.builder()
                .title("Test Post")
                .text("Test Content")
                .tags(List.of("java", "spring"))
                .build();

        String response = mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PostDto result = objectMapper.readValue(response, PostDto.class);
        assertThat(result.getTitle()).isEqualTo("Test Post");
        assertThat(result.getText()).isEqualTo("Test Content");
        assertThat(result.getLikesCount()).isZero();
    }

    @Test
    void shouldGetPost() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post 1", "Content 1", "tag1,tag2", 0, 0);

        String response = mockMvc.perform(post("/api/posts/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PostDto result = objectMapper.readValue(response, PostDto.class);
        assertThat(result.getTitle()).isEqualTo("Post 1");
        assertThat(result.getText()).isEqualTo("Content 1");
    }

    @Test
    void shouldGetPosts() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Java Post", "Java Content", "java", 0, 0);
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Spring Post", "Spring Content", "spring", 0, 0);

        String response = mockMvc.perform(get("/api/posts")
                        .param("search", "java")
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PostPageResponse result = objectMapper.readValue(response, PostPageResponse.class);
        assertThat(result.getPosts()).hasSize(1);
        assertThat(result.getPosts().get(0).getTitle()).isEqualTo("Java Post");
    }

    @Test
    void shouldUpdatePost() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Old Title", "Old Content", "old", 0, 0);

        PostUpdateRequest request = PostUpdateRequest.builder()
                .id(1L)
                .title("New Title")
                .text("New Content")
                .tags(List.of("new"))
                .build();

        String response = mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PostDto result = objectMapper.readValue(response, PostDto.class);
        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getText()).isEqualTo("New Content");
    }

    @Test
    void shouldDeletePost() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post to Delete", "Content", "tag", 0, 0);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenPostNotFound() throws Exception {
        mockMvc.perform(post("/api/posts/999"))
                .andExpect(status().isNotFound());
    }
}
