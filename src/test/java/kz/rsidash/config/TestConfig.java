package kz.rsidash.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "kz.rsidash")
@PropertySource("classpath:application-test.properties")
public class TestConfig {

    @Bean
    public DataSource dataSource() {
        final var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        DatabasePopulatorUtils.execute(databasePopulator(), dataSource);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    private DatabasePopulator databasePopulator() {
        final var populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));
        return populator;
    }
}
