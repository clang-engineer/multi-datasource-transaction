package com.example.demo.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class SecondaryDatasourceConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.secondary")
  public DataSource secondaryDataSource() {
    return new AtomikosDataSourceBean();
  }

  @Bean
  public JdbcTemplate secondaryJdbcTemplate(
      @Qualifier("secondaryDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
