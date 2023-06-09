package com.example.demo.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class PrimaryDatasourceConfiguration {

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.primary")
  public DataSource primaryDataSource() {
    return new AtomikosDataSourceBean();
  }

  @Bean
  @Primary
  public JdbcTemplate primaryJdbcTemplate(@Qualifier("primaryDataSource") DataSource dataSource) {
    return new JdbcTemplate(dataSource);
  }
}
