package com.vrs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author dam
 * @create 2024/11/15 16:25
 */
@SpringBootApplication
@MapperScan("com.vrs.mapper")
public class VrsAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(VrsAdminApplication.class, args);
    }
}