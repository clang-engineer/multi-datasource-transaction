package com.example.demo.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql("classpath:create_table.sql")
public class JdbcTemplateTest {
    @Autowired
    @Qualifier("primaryJdbcTemplate")
    private JdbcTemplate primaryJdbcTemplate;

    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    private JdbcTemplate secondaryJdbcTemplate;

    @BeforeEach
    public void setUp() {
        primaryJdbcTemplate.execute("INSERT INTO test_table VALUES (1, 'test1')");
    }

    @Test
    @Transactional
    public void testPrimaryJdbcTemplate() {
        String query = "SELECT COUNT(*) FROM test_table";
        int result = primaryJdbcTemplate.queryForObject(query, Integer.class);
        assertThat(result).isEqualTo(1);
    }

    @Test
    @Transactional
    public void testSecondaryJdbcTemplate() {
        String query = "SELECT COUNT(*) FROM test_table";
        int result = secondaryJdbcTemplate.queryForObject(query, Integer.class);
        assertThat(result).isEqualTo(1);
    }
}