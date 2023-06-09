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
class DatasourceTransactionTest {

  @Autowired
  @Qualifier("primaryJdbcTemplate")
  private JdbcTemplate primaryJdbcTemplate;

  @Autowired
  @Qualifier("secondaryJdbcTemplate")
  private JdbcTemplate secondaryJdbcTemplate;

  private final String SELECT_ALL_TEST_TABLE = "SELECT COUNT(*) FROM test_table";

  private final String INSERT_INTO_TEST_TABLE = "INSERT INTO TEST_TABLE VALUES (%d, 'test%d')";

  @Test
  @Transactional
  void testPrimaryJdbcTemplate() {
    primaryJdbcTemplate.execute(String.format(INSERT_INTO_TEST_TABLE, 1, 1));
    Integer result = primaryJdbcTemplate.queryForObject(SELECT_ALL_TEST_TABLE, Integer.class);
    assertThat(result).isEqualTo(1);
  }

  @Test
  @Transactional
  void testSecondaryJdbcTemplate() {
    secondaryJdbcTemplate.execute(String.format(INSERT_INTO_TEST_TABLE, 1, 1));
    Integer result = secondaryJdbcTemplate.queryForObject(SELECT_ALL_TEST_TABLE, Integer.class);
    assertThat(result).isEqualTo(1);
  }

  @Test
  @Transactional
  void testTransactionRollback() {
    try {
      insertAndRollback();
    } catch (Exception e) {
      assertThat(primaryJdbcTemplate.queryForObject(SELECT_ALL_TEST_TABLE, Integer.class))
          .isZero();
      assertThat(secondaryJdbcTemplate.queryForObject(SELECT_ALL_TEST_TABLE, Integer.class))
          .isZero();
    }
  }

  @Transactional
  protected void insertAndRollback() {
    try {
      primaryJdbcTemplate.execute(String.format(INSERT_INTO_TEST_TABLE, 1, 1));
      secondaryJdbcTemplate.execute(String.format(INSERT_INTO_TEST_TABLE, 2, 2));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}