package com.vrs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Author dam
 * @create 2024/11/15 16:38
 */
@SpringBootApplication
@MapperScan("com.vrs.mapper")
@EnableScheduling
@EnableDiscoveryClient
// 指定feign扫描路径
@EnableFeignClients("com.vrs.feign")
@EnableTransactionManagement
public class VrsOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(VrsOrderApplication.class, args);
    }
}