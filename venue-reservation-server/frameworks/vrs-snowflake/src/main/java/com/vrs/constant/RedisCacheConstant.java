package com.vrs.constant;

/**
 * Redis Key 缓存常量类
 *
 * 该类来源于马哥的开源项目 12306（代码仓库：https://gitee.com/nageoffer/12306，该项目含金量较高，有兴趣的朋友们建议去学习一下)
 * 本人的工作：对类进行详细注释，或者针对本项目做了部分代码改动。
 */
public class RedisCacheConstant {

    public static final String PREFIX = "vrs:";
    
    /**
     * 雪花算法ID
     */
    public static final String SNOWFLAKE_WORK_ID_KEY = PREFIX + "snowflake_work_id_key";
}
