package com.vrs.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 场区状态枚举
 */
@RequiredArgsConstructor
public enum PartitionStatusEnum {

    CLOSED(0, "关闭"),
    OPEN(1, "开放"),
    MAINTAIN(2, "维护中");

    @Getter
    private final int type;

    @Getter
    private final String value;

    /**
     * 根据 type 找到对应的 value
     *
     * @param type 要查找的类型代码
     * @return 对应的描述值，如果没有找到抛异常
     */
    public static String findValueByType(int type) {
        for (PartitionStatusEnum target : PartitionStatusEnum.values()) {
            if (target.getType() == type) {
                return target.getValue();
            }
        }
        throw new IllegalArgumentException();
    }
}
