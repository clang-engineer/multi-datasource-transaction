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

  private String SELECT_COUNT_SQL = "SELECT COUNT(*) FROM test_table";

  private String INSERT_SQL = "INSERT INTO TEST_TABLE VALUES (%d, 'test%d')";

  @Test
  @Transactional
  public void testPrimaryJdbcTemplate() {
    primaryJdbcTemplate.execute(String.format(INSERT_SQL, 1, 1));
    String query = SELECT_COUNT_SQL;
    int result = primaryJdbcTemplate.queryForObject(query, Integer.class);
    assertThat(result).isEqualTo(1);
  }

  @Test
  @Transactional
  public void testSecondaryJdbcTemplate() {
    secondaryJdbcTemplate.execute(String.format(INSERT_SQL, 1, 1));
    String query = SELECT_COUNT_SQL;
    int result = secondaryJdbcTemplate.queryForObject(query, Integer.class);
    assertThat(result).isEqualTo(1);
  }

  @Test
  @Transactional
  public void testTransactionRollback() {
    try {
      insertAndRollback();
    } catch (Exception e) {
      assertThat(primaryJdbcTemplate.queryForObject(SELECT_COUNT_SQL, Integer.class))
          .isEqualTo(0);
      assertThat(secondaryJdbcTemplate.queryForObject(SELECT_COUNT_SQL, Integer.class))
          .isEqualTo(0);
    }
  }

  @Transactional
  protected void insertAndRollback() {
    try {
      primaryJdbcTemplate.execute(String.format(INSERT_SQL, 1, 1));
      secondaryJdbcTemplate.execute(String.format(INSERT_SQL, 2, 2));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}