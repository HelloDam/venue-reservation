package com.vrs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @Author dam
 * @create 2024/11/15 16:22
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class VrsGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(VrsGatewayApplication.class, args);
    }
}