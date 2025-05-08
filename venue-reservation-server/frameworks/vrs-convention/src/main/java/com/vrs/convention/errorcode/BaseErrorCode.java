package com.vrs.convention.errorcode;

/**
 * 基础错误码定义
 */
public enum BaseErrorCode implements IErrorCode {

    //// ========== 一级宏观错误码 客户端错误 ==========
    CLIENT_ERROR("A000001", "用户端错误"),

    /// ========== 二级宏观错误码 用户注册错误 ==========
    USER_REGISTER_ERROR("A000100", "用户注册错误"),
    USER_NAME_VERIFY_ERROR("A000110", "用户名校验失败"),
    USER_NAME_EXIST_ERROR("A000111", "用户名已存在"),
    USER_NULL_ERROR("A000112", "用户记录不存在"),
    USER_EXIST_ERROR("A000113", "用户记录已存在"),
    USER_TOKEN_EXPIRE("A000114", "用户登录状态过期，请重新登录"),
    USER_TOKEN_FAIL("A000115", "用户token异常，请重新登录"),
    WECHAT_LOGIN_FAIL("A000116", "微信登录失败"),
    USER_NAME_SENSITIVE_ERROR("A000130", "用户名包含敏感词"),
    USER_NAME_SPECIAL_CHARACTER_ERROR("A000131", "用户名包含特殊字符"),
    PASSWORD_VERIFY_ERROR("A000132", "密码校验失败"),
    PASSWORD_SHORT_ERROR("A000133", "密码长度不够"),
    PHONE_VERIFY_ERROR("A000134", "手机格式校验失败"),

    /// ========== 二级宏观错误码 系统请求缺少幂等Token ==========
    IDEMPOTENT_ERROR("A000200", "接口重复调用"),

    /// ========== 二级宏观错误码 系统请求操作频繁 ==========
    FLOW_LIMIT_ERROR("A000300", "请求过于繁忙，请稍后再试"),

    /// ========== 二级宏观错误码 文件上传错误 ==========
    PICTURE_NAME_EXCEED_LENGTH("A000400", "图片名超出长度"),
    NO_SUFFIX_ERROR("A000401", "所上传文件没有携带正常后缀名"),
    PICTURE_TYPE_ERROR("A000402", "图片格式不对，仅限于 .png .jpg .jpeg .gif"),

    EXCEL_TYPE_ERROR("A000405", "excel文件格式不对，仅限于 .xlsx .xls"),

    /// ========== 二级宏观错误码 用户权限校验错误 ==========
    USER_TYPE_IS_NOT_SYSTEM_MANAGER_ERROR("A000500", "当前用户不具备系统管理员权限，无法进行当前操作"),
    USER_TYPE_IS_NOT_INSTITUTE_MANAGER_ERROR("A000501", "当前用户不具备机构管理员权限，无法进行当前操作"),
    USER_TYPE_IS_NOT_RIGHT_ERROR("A000502", "当前用户不具备此操作的权限"),

    /// ========== 二级宏观错误码 用户信息校验错误 ==========
    USER_NOT_SET_ORGANIZATION_ERROR("A000600", "当前用户没有绑定任何机构"),
    USER_NOT_BELONG_ORGANIZATION_ERROR("A000601", "用户不属于当前组织机构，无法预定"),

    /// ========== 二级宏观错误码 时间段错误 ==========
    TIME_PERIOD_NULL_ERROR("A000700", "所预定时间段不存在"),
    TIME_PERIOD_MISS_ERROR("A000701", "当前时间段已经过了可预定的截止时间，请选择其他时间段"),
    TIME_NOT_ARRIVE_RESERVE_ERROR("A000702", "当前时间段还未到达可预订时间，请后面再来"),
    TIME_PERIOD_HAVE_BOUGHT_ERROR("A000703", "已经购买过当前时间段，不允许重复购买"),
    COURT_ERROR_ERROR("A000704", "所预定场号有误"),

    /// ========== 二级宏观错误码 场馆错误 ==========
    VENUE_NULL_ERROR("A000800", "当前场馆不存在"),
    /// ========== 二级宏观错误码 场馆错误 ==========
    PARTITION_NULL_ERROR("A000900", "当前分区不存在"),

    //// ========== 一级宏观错误码 系统执行错误 ==========
    SERVICE_ERROR("B000001", "系统执行出错"),
    /// ========== 二级宏观错误码 系统执行超时 ==========
    SERVICE_TIMEOUT_ERROR("B000100", "系统执行超时"),

    /// ========== 二级宏观错误码 用户相关 ==========
    USER_SAVE_ERROR("B000200", "用户记录新增失败"),

    /// ========== 二级宏观错误码 图片上传错误 ==========
    PICTURE_UPLOAD_FAIL("B000300", "图片上传失败"),

    /// ========== 二级宏观错误码 时间段错误 ==========
    TIME_PERIOD_SELL_OUT_ERROR("B000401", "所预定时间段已经售罄，请下次再来"),
    TIME_PERIOD_HAVE_NOT_BOUGHT_ERROR("B000402", "该时间段没有被该用户购买"),
    TIME_PERIOD_FREE_COURT_INDEX_ERROR("B000403", "场号存在问题"),
    TIME_PERIOD_COURT_HAVE_BEEN_BOUGHT_ERROR("B000404", "所挑选场地已经被其他用户抢先预定"),

    /// ========== 二级宏观错误码 订单错误 ==========
    ORDER_GENERATE_ERROR("B000700", "订单生成失败"),
    ORDER_NULL_ERROR("B000701", "当前订单不存在"),
    ORDER_NOT_PAID_ERROR("B000702", "当前订单未支付"),
    ORDER_EXPIRE_ERROR("B000703", "当前订单已过期"),
    ORDER_NOT_ARRIVE_DATE_ERROR("B000704", "还没有到该订单预定的日期，无法生成二维码"),
    ORDER_HAS_PAID_ERROR("B000705", "当前订单已支付"),
    ORDER_HAS_CANCELED_ERROR("B000706", "当前订单已取消"),
    ORDER_HAS_REFUND_ERROR("B000707", "当前订单已退款"),
    ORDER_NOT_TRANSACTION_ERROR("B000708", "当前订单未进行交易"),


    /// ========== 二级宏观错误码 消息发送错误 ==========
    MQ_SEND_ERROR("B001000", "消息发送失败"),


    // ========== 一级宏观错误码 调用第三方服务出错 ==========
    REMOTE_ERROR("C000001", "调用第三方服务出错");

    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
