package com.vrs.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.vrs.utils.SnowflakeIdUtil;
import org.springframework.stereotype.Component;

/**
 * 配置使用雪花算法来生成ID
 * @Author dam
 * @create 2024/12/5 19:54
 */
@Component
public class CustomIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return SnowflakeIdUtil.nextId();
    }

}
