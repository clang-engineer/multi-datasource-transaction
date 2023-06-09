package com.example.demo.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import javax.transaction.UserTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
public class JtaTransactionManagerConfiguration {
  @Bean
  public PlatformTransactionManager transactionManager() throws Exception {
    UserTransaction userTransaction = new UserTransactionImp();
    userTransaction.setTransactionTimeout(10000);

    UserTransactionManager userTransactionManager = new UserTransactionManager();
    userTransactionManager.setForceShutdown(false);

    JtaTransactionManager manager = new JtaTransactionManager(
        userTransaction, userTransactionManager);
    return manager;
  }
}