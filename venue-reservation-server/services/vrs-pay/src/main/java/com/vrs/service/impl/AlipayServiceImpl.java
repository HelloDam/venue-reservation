package com.vrs.service.impl;

import cn.hutool.core.lang.Singleton;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.diagnosis.DiagnosisUtils;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.vrs.common.constant.PayTypeConstant;
import com.vrs.common.constant.RefundTypeConstant;
import com.vrs.common.dto.PayCallbackDTO;
import com.vrs.config.AlipayConfig;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ClientException;
import com.vrs.domain.dto.mq.OrderPayMqDTO;
import com.vrs.domain.dto.mq.OrderRefundMqDTO;
import com.vrs.domain.dto.req.AlipayInfoReqDTO;
import com.vrs.domain.dto.req.AlipayPayReqDTO;
import com.vrs.domain.dto.req.AlipayRefundReqDTO;
import com.vrs.domain.dto.resp.AlipayInfoRespDTO;
import com.vrs.domain.dto.resp.AlipayRefundRespDTO;
import com.vrs.domain.entity.PayDO;
import com.vrs.rocketMq.producer.OrderPayProducer;
import com.vrs.rocketMq.producer.OrderRefundProducer;
import com.vrs.service.AlipayService;
import com.vrs.service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author dam
 * @create 2024/12/31 14:11
 */
@Service
@RequiredArgsConstructor
public class AlipayServiceImpl implements AlipayService {

    private final AlipayConfig alipayConfig;
    private final PayService payService;
    private final OrderRefundProducer orderRefundProducer;
    private final OrderPayProducer orderPayProducer;

    // 定义日期时间格式
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @SneakyThrows
    @Override
    public String commonPay(AlipayPayReqDTO alipayPayReqDTO) {
        String alipayUrl = alipay(alipayPayReqDTO);

        // 存储支付信息到数据库中
        PayDO payDO = PayDO.builder()
                .orderSn(alipayPayReqDTO.getOrderSn())
                .paymentMethod(PayTypeConstant.ALIPAY)
                .subject(alipayPayReqDTO.getSubject())
                .build();
        try {
            // 捕捉异常，避免重复发起支付，抛出唯一索引异常，导致程序中断
            payService.save(payDO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alipayUrl;
    }

    /**
     * 支付宝支付
     *
     * @param alipayPayReqDTO
     * @return
     * @throws AlipayApiException
     */
    private String alipay(AlipayPayReqDTO alipayPayReqDTO) throws AlipayApiException {
        // 使用 Hutool 单例模式来管理
        AlipayClient alipayClient = Singleton.get("alipayClient", () -> {
            return new DefaultAlipayClient(
                    alipayConfig.getGatewayUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getPrivateKey(),
                    alipayConfig.getFormat(),
                    alipayConfig.getCharset(),
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getSignType());
        });

        // 实例化具体API对应的request类，类名称和接口名称对应,当前调用接口名称 alipay.trade.wap.pay
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();

        JSONObject content = new JSONObject();

        /// 必传参数
        // 商户订单号,商户自定义，需保证在商户端不重复，如：20200612000001
        content.put("out_trade_no", alipayPayReqDTO.getOrderSn());
        // 订单标题
        content.put("subject", alipayPayReqDTO.getSubject());
        // 订单金额，精确到小数点后两位
        content.put("total_amount", alipayPayReqDTO.getPayAmount());
        /// 可选参数
        // 销售产品码,固定值：ALIPAY_WAP_PAY
        content.put("product_code", "ALIPAY_WAP_PAY");
        // 订单超时时间，3分钟之后过期
        content.put("time_expire", generateTimeExpire(3));

        // 封装请求参数到biz_content
        request.setBizContent(content.toString());

        // 注：支付结果以异步通知为准，不能以同步返回为准，因为如果实际支付成功，但因为外力因素，如断网、断电等导致页面没有跳转，则无法接收到同步通知；
        // 支付完成的跳转地址,用于用户视觉感知支付是否完成，传值外网可以访问的地址
        request.setReturnUrl(alipayPayReqDTO.getReturnUrl());

        // 异步通知地址，以http或者https开头的，商户外网可以post访问的异步地址，用于接收支付宝返回的支付结果
        request.setNotifyUrl(alipayConfig.getNotifyUrl());

        // 第三方调用（服务商模式），传值app_auth_token后，会收款至授权token对应商家账号
        request.putOtherTextParam("app_auth_token", alipayConfig.getAppAuthToken());

        // 生成form表单
        // AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
        // 生成url链接
        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "GET");

        // 获取接口调用结果
        return response.getBody();
    }

    /**
     * 生成交易过期时间
     *
     * @param minuteNum
     * @return
     */
    private String generateTimeExpire(int minuteNum) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 加上3分钟
        LocalDateTime threeMinutesLater = now.plus(minuteNum, ChronoUnit.MINUTES);

        // 格式化时间为字符串
        return threeMinutesLater.format(formatter);
    }

    /**
     * 支付之后的回调方法
     *
     * @param payCallbackDTO
     */
    @Override
    public void callback(PayCallbackDTO payCallbackDTO) {
        if (payCallbackDTO.getTradeStatus().equals("TRADE_SUCCESS")) {
            // --if-- 支付成功
            QueryWrapper<PayDO> payDOQueryWrapper = new QueryWrapper<>();
            payDOQueryWrapper.eq("order_sn", payCallbackDTO.getOutTradeNo());
            payService.update(PayDO.builder()
                    .payAmount(payCallbackDTO.getBuyerPayAmount())
                    .payTime(payCallbackDTO.getGmtPayment())
                    .transactionId(payCallbackDTO.getTradeNo())
                    .build(), payDOQueryWrapper);
            // 发送消息，通知订单服务，修改订单状态为已支付状态
            orderPayProducer.sendMessage(OrderPayMqDTO.builder()
                    .orderSn(payCallbackDTO.getOutTradeNo())
                    .build());
        }
    }

    @SneakyThrows
    @Override
    public AlipayRefundRespDTO commonRefund(AlipayRefundReqDTO alipayRefundReqDTO) {
        // 初始化SDK
        AlipayClient alipayClient = Singleton.get("alipayClient", () -> {
            return new DefaultAlipayClient(
                    alipayConfig.getGatewayUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getPrivateKey(),
                    alipayConfig.getFormat(),
                    alipayConfig.getCharset(),
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getSignType());
        });

        QueryWrapper<PayDO> payDOQueryWrapper = new QueryWrapper<>();
        payDOQueryWrapper.eq("order_sn", alipayRefundReqDTO.getOrderSn());
        PayDO payDO = payService.getOne(payDOQueryWrapper);
        if (payDO == null) {
            // --if-- 订单未支付
            throw new ClientException(BaseErrorCode.ORDER_NOT_PAID_ERROR);
        }

        // 构造请求参数以调用接口
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        // 设置支付宝交易号
//        model.setOutTradeNo(alipayRefundReqDTO.getOrderSn());
        model.setTradeNo(payDO.getTransactionId());
        // 设置退款金额
        model.setRefundAmount(alipayRefundReqDTO.getRefundAmount().toString());
        // 设置退款原因说明
        model.setRefundReason("正常退款");
        // 退款请求号（如果分多笔退款，必须设置改参数，且同一订单的一致）
        model.setOutRequestNo(alipayRefundReqDTO.getOrderSn());
        request.setBizModel(model);

        AlipayTradeRefundResponse response = alipayClient.execute(request);
        System.out.println(response.getBody());

        if (response.isSuccess()) {
            System.out.println("退款成功");
            payService.update(PayDO.builder()
                    .refundAmount(alipayRefundReqDTO.getRefundAmount())
                    .payTime(response.getGmtRefundPay())
                    .refundStatus(RefundTypeConstant.FULL_REFUND)
                    .build(), payDOQueryWrapper);
            // 发送消息，通知订单服务，修改订单状态为已退款状态
            orderRefundProducer.sendMessage(OrderRefundMqDTO.builder()
                    .orderSn(alipayRefundReqDTO.getOrderSn())
                    .build());
        } else {
            System.out.println("退款失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            System.out.println(diagnosisUrl);
        }
        AlipayRefundRespDTO refundRespDTO = new AlipayRefundRespDTO();
        BeanUtils.copyProperties(response, refundRespDTO);
        refundRespDTO.setSuccess(response.getCode().equals("10000") && response.getFundChange().equals("Y"));
        refundRespDTO.setRefundFee(new BigDecimal(response.getRefundFee()));
        return refundRespDTO;
    }

    @SneakyThrows
    @Override
    public AlipayInfoRespDTO info(AlipayInfoReqDTO alipayInfoReqDTO) {

        PayDO payDO = payService.getOne(Wrappers.lambdaQuery(PayDO.class).eq(PayDO::getOrderSn, alipayInfoReqDTO.getOrderSn()));
        if (payDO == null) {
            // --if-- 订单还未交易
            throw new ClientException(BaseErrorCode.ORDER_NOT_TRANSACTION_ERROR);
        }

        // 初始化SDK
        AlipayClient alipayClient = Singleton.get("alipayClient", () -> {
            return new DefaultAlipayClient(
                    alipayConfig.getGatewayUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getPrivateKey(),
                    alipayConfig.getFormat(),
                    alipayConfig.getCharset(),
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getSignType());
        });

        // 构造请求参数以调用接口
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        // 设置订单支付时传入的商户订单号
        model.setOutTradeNo(alipayInfoReqDTO.getOrderSn());

        // 设置支付宝交易号
//        model.setTradeNo("2025010222001498510504477448");

        // 设置查询选项
        List<String> queryOptions = new ArrayList<String>();
        queryOptions.add("trade_settle_info");
        model.setQueryOptions(queryOptions);

        request.setBizModel(model);

        AlipayTradeQueryResponse response = alipayClient.execute(request);
//        System.out.println(response.getBody());

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            System.out.println(diagnosisUrl);
        }

        AlipayInfoRespDTO alipayInfoRespDTO = new AlipayInfoRespDTO();
        BeanUtils.copyProperties(response, alipayInfoRespDTO);
        BeanUtils.copyProperties(payDO, alipayInfoRespDTO);
        return alipayInfoRespDTO;
    }
}
