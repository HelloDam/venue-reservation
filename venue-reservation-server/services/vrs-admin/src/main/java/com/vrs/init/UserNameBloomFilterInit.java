package com.vrs.init;

import com.vrs.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Author dam
 * @create 2025/1/6 9:59
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserNameBloomFilterInit implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        log.info("读取数据库中的用户名，将其添加到布隆过滤器中，避免切换Redis时，布隆过滤器信息不完全");
        userService.userNameBloomFilterInit();
        log.info("布隆过滤器初始化成功");
    }
}
