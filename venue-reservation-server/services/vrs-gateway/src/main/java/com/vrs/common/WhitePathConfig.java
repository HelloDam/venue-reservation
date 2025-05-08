package com.vrs.common;

import lombok.Data;

import java.util.List;

/**
 * @Author dam
 * @create 2024/11/16 18:13
 */
@Data
public class WhitePathConfig {
    /**
     * 白名单前置路径
     */
    private List<String> whitePathList;
}
