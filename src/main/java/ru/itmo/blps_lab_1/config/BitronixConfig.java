package ru.itmo.blps_lab_1.config;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;

@Configuration
@EnableTransactionManagement
public class BitronixConfig {
    @Bean
    public BitronixTransactionManager
        bitronixTransactionManager() throws Throwable {
       BitronixTransactionManager bitronixTransactionManager = TransactionManagerServices.getTransactionManager();

       return bitronixTransactionManager;
    }

    @Bean
    @DependsOn({"bitronixTransactionManager"})
    public PlatformTransactionManager
        transactionManager(TransactionManager bitronixTransactionManager) throws Throwable{
        return new JtaTransactionManager(bitronixTransactionManager);
    }
}

