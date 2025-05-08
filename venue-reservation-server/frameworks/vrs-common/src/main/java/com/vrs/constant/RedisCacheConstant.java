package com.vrs.constant;

/**
 * Redis缓存Key常量类
 */
public class RedisCacheConstant {

    public static final String PREFIX = "vrs:";

    public static final String SEPARATE = ":";

    /**
     * 用户注册分布式锁
     */
    public static final String LOCK_USER_REGISTER_KEY = PREFIX + "lock_user-register:";

    /**
     * 用户登录缓存标识
     */
    public static final String USER_LOGIN_KEY = PREFIX + "login:";

    //// 场馆服务
    public static final String VENUE_PREFIX = "venue:";
    /**
     * 时间段key
     */
    public static final String VENUE_TIME_PERIOD_KEY = PREFIX + VENUE_PREFIX + "time_period:%s";
    /**
     * 时间段库存key
     */
    public static final String VENUE_TIME_PERIOD_STOCK_KEY = PREFIX + VENUE_PREFIX + "time_period_stock:%s";
    /**
     * 时间段库存key（令牌）
     */
    public static final String VENUE_TIME_PERIOD_STOCK_TOKEN_KEY = PREFIX + VENUE_PREFIX + "time_period_stock_token:%s";
    /**
     * 时间段对应的空闲的场号（位图版本）
     */
    public static final String VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY = PREFIX + VENUE_PREFIX + "time_period_free_index_bit_map:%s";
    /**
     * 时间段对应的空闲的场号（位图版本，令牌）
     */
    public static final String VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_TOKEN_KEY = PREFIX + VENUE_PREFIX + "time_period_free_index_bit_map_token:%s";
    /**
     * 时间段对应的空闲的场号 分布式锁
     */
    public static final String VENUE_LOCK_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY = PREFIX + VENUE_PREFIX + "lock_time_period_free_index_bit_map:%s";
    /**
     * 刷新时间段的令牌 分布式锁
     */
    public static final String VENUE_LOCK_TIME_PERIOD_REFRESH_TOKEN_KEY = PREFIX + VENUE_PREFIX + "lock_time_period_token_fresh:%s";
    /**
     * 场区的相关时间段
     */
    public static final String VENUE_TIME_PERIOD_BY_PARTITION_ID_KEY = PREFIX + VENUE_PREFIX + "time_period_by_partition_id:%s";
    /**
     * 指定日期、场区的相关时间段
     */
    public static final String VENUE_TIME_PERIOD_BY_PARTITION_ID_AND_DATE_KEY = PREFIX + VENUE_PREFIX + "time_period_by_partition_id:%s_by_date:%s";
    /**
     * 根据分区ID获取场馆ID
     */
    public static final String VENUE_PARTITION_ID_TO_VENUE_ID_KEY = PREFIX + VENUE_PREFIX + "partitionId_to_venue_id:%s";
    /**
     * 根据ID获取场馆信息
     */
    public static final String VENUE_GET_VENUE_BY_ID_KEY = PREFIX + VENUE_PREFIX + "get_venue_by_id:%s";
    /**
     * 根据分区ID获取对应场馆ID
     */
    public static final String VENUE_GET_VENUE_ID_BY_PARTITION_ID_KEY = PREFIX + VENUE_PREFIX + "get_venue_by_partition_id:%s";
    /**
     * 根据ID获取场区信息
     */
    public static final String VENUE_GET_PARTITION_BY_ID_KEY = PREFIX + VENUE_PREFIX + "get_partition_by_id:%s";
    /**
     * 用户是否购买过
     */
    public static final String VENUE_IS_USER_BOUGHT_TIME_PERIOD_KEY = PREFIX + VENUE_PREFIX + "is_user_bought_time_period:%s";
    /**
     * 时间段模板表是否已经被扫描
     */
    public static final String VENUE_TIME_PERIOD_GENERATE_KEY = PREFIX + VENUE_PREFIX + "time_period_generate_key:%s";
    /**
     * 场馆位置缓存
     */
    public static final String VENUE_LOCATION_KEY = PREFIX + VENUE_PREFIX + "location";

    //// 订单服务
    public static final String ORDER_PREFIX = "order:";
    /**
     * 订单支付锁定
     */
    public static final String ORDER_PAY_LOCK_KEY = PREFIX + ORDER_PREFIX + "pay_lock:%s";

    ////////// 限流 ///////////
    public static final String IP_FLOW_CONTROL = PREFIX + "ip-flow-control";
}
