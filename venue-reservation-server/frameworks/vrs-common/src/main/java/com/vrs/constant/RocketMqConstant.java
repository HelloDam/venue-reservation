package com.vrs.constant;

/**
 * @Author dam
 * @create 2024/12/1 15:50
 */
public class RocketMqConstant {

    //// 系统管理模块
    // 系统管理服务topic
    public static final String ADMIN_TOPIC = "vrs_admin_topic";
    // 系统管理服务消费者组
    public static final String ADMIN_CONSUMER_GROUP = "vrs_admin_consumer_group";
    // 机构用户导入标签
    public static final String IMPORT_USER_EXCEL_TAG = "import_user_excel_tag";

    //// 场馆管理模块
    // 场馆管理服务topic
    public static final String VENUE_TOPIC = "vrs_venue_topic";
    // 场馆管理服务消费者组
    public static final String VENUE_CONSUMER_GROUP = "vrs_venue_consumer_group";
    // 扣减时间段库存和已预定位置修改
    public static final String TIME_PERIOD_STOCK_REDUCE_TAG = "time_period_stock_restore_tag";
    // 时间段库存和已预定位置恢复标签
    public static final String TIME_PERIOD_STOCK_RESTORE_TAG = "time_period_stock_restore_tag";
    // 时间段预订标签
    public static final String TIME_PERIOD_EXECUTE_RESERVE_TAG = "time_period_stock_update_tag";
    // websocket 发送消息标签
    public static final String WEBSOCKET_SEND_MESSAGE_TAG = "websocket_send_message_tag";

    //// 订单模块
    // 订单服务topic
    public static final String ORDER_TOPIC = "vrs_order_topic";
    // 场馆管理服务消费者组
    public static final String ORDER_CONSUMER_GROUP = "vrs_order_consumer_group";
    // 订单延时关闭标签
    public static final String ORDER_DELAY_CLOSE_TAG = "order_delay_close_tag";
    // 第二次订单延时关闭标签
    public static final String ORDER_SECOND_DELAY_CLOSE_TAG = "order_second_delay_close_tag";
    // 修改订单为已支付标签
    public static final String ORDER_PAY_TAG = "order_pay_tag";
    // 修改订单为已退款标签
    public static final String ORDER_REFUND_TAG = "order_refund_tag";

    //// canal
    public static final String CANAL_TOPIC = "vrs_canal_common_topic";
    public static final String CANAL_CONSUMER_GROUP = "vrs_canal_consumer_group";
}
