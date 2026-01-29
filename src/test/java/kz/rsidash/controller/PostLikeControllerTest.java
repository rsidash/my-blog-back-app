package kz.rsidash.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.rsidash.config.TestConfig;
import kz.rsidash.dto.post.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
class PostLikeControllerTest {

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
    void shouldLikePost() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post", "Content", "tag", 0, 0);

        String response = mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PostDto result = objectMapper.readValue(response, PostDto.class);
        assertThat(result.getLikesCount()).isEqualTo(1);
    }

    @Test
    void shouldIncrementLikesMultipleTimes() throws Exception {
        jdbcTemplate.update("INSERT INTO posts (title, text, tags, likesCount, commentsCount) VALUES (?, ?, ?, ?, ?)",
                "Post", "Content", "tag", 0, 0);

        String response1 = mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PostDto result1 = objectMapper.readValue(response1, PostDto.class);
        assertThat(result1.getLikesCount()).isEqualTo(1);

        String response2 = mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        PostDto result2 = objectMapper.readValue(response2, PostDto.class);
        assertThat(result2.getLikesCount()).isEqualTo(2);
    }
}
