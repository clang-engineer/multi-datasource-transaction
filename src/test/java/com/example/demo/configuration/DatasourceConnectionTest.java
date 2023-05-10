package com.example.demo.configuration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Sql(scripts = "classpath:/create_table.sql")
public class DatasourceConnectionTest {

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;

    @Autowired
    @Qualifier("secondaryDataSource")
    private DataSource secondaryDataSource;

    @Test
    public void primaryConnection() throws SQLException {
        Connection connection = primaryDataSource.getConnection();
        Assertions.assertThat(connection).isNotNull();
        connection.close();
    }

    @Test
    public void secondaryConnection() throws SQLException {
        Connection connection = secondaryDataSource.getConnection();
        Assertions.assertThat(connection).isNotNull();
        connection.close();
    }

}
