package com.jarviswuod.improvedbillingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public abstract class AbstractTestContainerTest {

    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer(DockerImageName.parse("postgres:16.2"));


    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    void clearDatabase() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = 'public' " +
                        "AND table_name NOT LIKE 'flyway_%' " +
                        "AND table_name NOT LIKE 'schema_version%'",
                String.class
        );

        if (!tables.isEmpty()) {
            String tableList = String.join(", ", tables);

            jdbcTemplate.execute("TRUNCATE TABLE " + tableList + " RESTART IDENTITY CASCADE;");
        }
    }


    @Test
    void connectionEstablished() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }
}
