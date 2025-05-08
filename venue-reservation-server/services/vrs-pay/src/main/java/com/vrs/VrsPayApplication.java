package com.vrs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author dam
 * @create 2024/11/15 16:38
 */
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.vrs.mapper")
@EnableTransactionManagement
public class VrsPayApplication {
    public static void main(String[] args) {
        SpringApplication.run(VrsPayApplication.class, args);
    }
}