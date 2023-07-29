package com.hk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan(value = {"com.hk.mapper"})
@SpringBootApplication(scanBasePackages = {"com.hk"})
@EnableTransactionManagement
public class HkbbsWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(HkbbsWebApplication.class,args);
    }
}
