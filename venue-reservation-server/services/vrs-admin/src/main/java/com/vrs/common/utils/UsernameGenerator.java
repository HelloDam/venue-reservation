package com.vrs.common.utils;

import java.util.Random;

/**
 * @Author dam
 * @create 2025/1/9 14:57
 */
public class UsernameGenerator {
    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int USERNAME_LENGTH = 5; // 用户名的长度

    public static String generateUsername() {
        StringBuilder username = new StringBuilder();
        Random random = new Random();

        // 添加一个固定的前缀
        username.append("WX_");

        // 生成随机字符串
        for (int i = 0; i < USERNAME_LENGTH; i++) {
            int index = random.nextInt(ALPHA_NUMERIC_STRING.length());
            username.append(ALPHA_NUMERIC_STRING.charAt(index));
        }

        // 使用当前时间戳确保唯一性
        long currentTimeMillis = System.currentTimeMillis();
        username.append("_").append(currentTimeMillis);

        return username.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateUsername());
    }
}
