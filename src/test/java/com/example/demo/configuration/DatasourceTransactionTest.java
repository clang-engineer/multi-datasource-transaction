package com.example.demo.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql("classpath:create_table.sql")
public class DatasourceTransactionTest {

  @Autowired
  @Qualifier("primaryJdbcTemplate")
  private JdbcTemplate primaryJdbcTemplate;

  @Autowired
  @Qualifier("secondaryJdbcTemplate")
  private JdbcTemplate secondaryJdbcTemplate;

  @Test
  @Transactional
  public void testPrimaryJdbcTemplate() {
    primaryJdbcTemplate.execute("INSERT INTO test_table VALUES (1, 'test1')");
    String query = "SELECT COUNT(*) FROM test_table";
    int result = primaryJdbcTemplate.queryForObject(query, Integer.class);
    assertThat(result).isEqualTo(1);
  }

  @Test
  @Transactional
  public void testSecondaryJdbcTemplate() {
    secondaryJdbcTemplate.execute("INSERT INTO test_table VALUES (1, 'test1')");
    String query = "SELECT COUNT(*) FROM test_table";
    int result = secondaryJdbcTemplate.queryForObject(query, Integer.class);
    assertThat(result).isEqualTo(1);
  }

  @Test
  @Transactional
  public void testTransactionRollback() {
    try {
      primaryJdbcTemplate.update("INSERT INTO test_table (column1) VALUES (1, 'value1')");
      secondaryJdbcTemplate.update("INSERT INTO test_table (column2) VALUES (2, 'value2')");

      throw new RuntimeException("Rollback Test");
    } catch (Exception e) {
      assertThat(primaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table", Integer.class))
          .isEqualTo(0);
      assertThat(secondaryJdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table", Integer.class))
          .isEqualTo(0);
    }
  }
}