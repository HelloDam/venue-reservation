package com.vrs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝配置文件
 *
 * @Author dam
 * @create 2024/12/31 9:35
 */
@Data
@Configuration
@ConfigurationProperties(prefix = AlipayConfig.PREFIX)
public class AlipayConfig {

    public static final String PREFIX = "vrs.pay.alipay";

    /**
     * 初始化代码配置信息
     **/
    //（必填）支付宝网关
    // 正式环境网关：https://openapi.alipay.com/gateway.do
    // 沙箱环境网关：https://openapi-sandbox.dl.alipaydev.com/gateway.do
    private String gatewayUrl;

    // （必填）应用ID
    // 请填写您的APPID:https://opendocs.alipay.com/common/02nebp
    private String appId;

    // （必填）应用私钥:https://opendocs.alipay.com/common/02kipk?pathHash=0d20b438
    // 请填写您的应用私钥，例如：MIIEvQIBADANB ...
    private String privateKey;


    /********** RSA2公钥模式签名必用，公钥证书签名不传 ************/
    /**
     * 注：如果采用非证书模式，则无需赋值三个证书路径，改为赋值如下的支付宝公钥字符串即可
     **/
    // 设置RSA2公钥方式：hhttps://opendocs.alipay.com/common/02kdnc?pathHash=fb0c752a

    // 支付宝公钥
    private String alipayPublicKey;


    /********** 公钥证书模式签名必用，RSA2公钥签名不传 ************/
    /**
     * 注：证书文件路径支持设置为文件系统中的路径或CLASS_PATH中的路径，同时配置公钥证书和RSA2公钥优先取公钥证书
     **/
    // 设置证书方式：https://opendocs.alipay.com/common/056zub?pathHash=91c49771

    // 应用公钥证书路径
    // 请填写您的应用公钥证书文件路径，例如：/foo/appCertPublicKey_2019051064521003.crt
    private String app_cert_path = "";

    // 支付宝公钥证书路径
    // 请填写您的支付宝公钥证书文件路径，例如：/foo/alipayCertPublicKey_RSA2.crt
    private String alipay_cert_path = "";

    // 支付宝根证书路径
    // 请填写您的支付宝根证书文件路径，例如：/foo/alipayRootCert.crt
    private String alipay_root_cert_path = "";


    //（必填）签名类型
    private String signType = "RSA2";

    //（必填）编码格式
    private String charset = "UTF-8";

    // 请求格式，固定值json
    private String format = "JSON";

    // 调用的接口版本，固定为：1.0
    private String version = "1.0";

    // AES密钥，配合encrypt_type=AES加解密相关接口
    private String encryptKey = "";

    // 请求格式，固定值AES，（设置EncryptKey时必选）
    private String encryptType = "AES";

    /**
     * 代码中其他配置信息，根据各产品API公共参数选择性引用
     **/
    // 第三方调用（服务商模式），传值app_auth_token后相当于以授权商户角色调用接口，app_auth_token获取流程：https://opendocs.alipay.com/isv/10467/xldcyq?pathHash=abce531a
    private String appAuthToken = "";

    // 通过公共参数notify_url配置上传异步地址
    // 异步通知地址需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，商户外网可以post访问的异步地址（不支持本地测试），用于接收支付宝返回的支付结果
    private String notifyUrl;

    // 通过接口公共参数return_url配置上传同步地址
    // 同步通知地址需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，get访问，用于支付完成后前端页面同步跳转
    private String returnUrl;

    // 日志记录目录
    private String log_path = "D:/log.txt";
}