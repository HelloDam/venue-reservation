package com.vrs;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.domain.ExtUserInfo;
import com.alipay.api.domain.ExtendParams;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;

import java.util.ArrayList;
import java.util.List;

public class AlipayTradeWapPay {

    public static void main(String[] args) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();

        // 设置商户订单号
        model.setOutTradeNo("70501111111S001111119");

        // 设置订单总金额
        model.setTotalAmount("9.00");

        // 设置订单标题
        model.setSubject("大乐透");

        // 设置产品码
        model.setProductCode("QUICK_WAP_WAY");

        // 设置针对用户授权接口
        model.setAuthToken("appopenBb64d181d0146481ab6a762c00714cC27");

        // 设置用户付款中途退出返回商户网站的地址
        model.setQuitUrl("http://www.taobao.com/product/113714.html");

        // 设置订单包含的商品列表信息
        List<GoodsDetail> goodsDetail = new ArrayList<GoodsDetail>();
        GoodsDetail goodsDetail0 = new GoodsDetail();
        goodsDetail0.setGoodsName("ipad");
        goodsDetail0.setAlipayGoodsId("20010001");
        goodsDetail0.setQuantity(1L);
        goodsDetail0.setPrice("2000");
        goodsDetail0.setGoodsId("apple-01");
        goodsDetail0.setGoodsCategory("34543238");
        goodsDetail0.setCategoriesTree("124868003|126232002|126252004");
        goodsDetail0.setBody("特价手机");
        goodsDetail0.setShowUrl("http://www.alipay.com/xxx.jpg");
        goodsDetail.add(goodsDetail0);
        model.setGoodsDetail(goodsDetail);

        // 设置订单绝对超时时间
        model.setTimeExpire("2026-12-31 10:05:00");

        // 设置业务扩展参数
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088511833207846");
        extendParams.setHbFqSellerPercent("100");
        extendParams.setHbFqNum("3");
        extendParams.setIndustryRefluxInfo("{\"scene_code\":\"metro_tradeorder\",\"channel\":\"xxxx\",\"scene_data\":{\"asset_name\":\"ALIPAY\"}}");
        extendParams.setRoyaltyFreeze("true");
        extendParams.setCardType("S0JP0000");
        model.setExtendParams(extendParams);

        // 设置商户传入业务信息
        model.setBusinessParams("{\"mc_create_trade_ip\":\"127.0.0.1\"}");

        // 设置公用回传参数
        model.setPassbackParams("merchantBizType%3d3C%26merchantBizNo%3d2016010101111");

        // 设置商户的原始订单号
        model.setMerchantOrderNo("20161008001");

        // 设置外部指定买家
        ExtUserInfo extUserInfo = new ExtUserInfo();
        extUserInfo.setCertType("IDENTITY_CARD");
        extUserInfo.setCertNo("362334768769238881");
        extUserInfo.setName("李明");
        extUserInfo.setMobile("16587658765");
        extUserInfo.setFixBuyer("F");
        extUserInfo.setMinAge("18");
        extUserInfo.setNeedCheckInfo("F");
        extUserInfo.setIdentityHash("27bfcd1dee4f22c8fe8a2374af9b660419d1361b1c207e9b41a754a113f38fcc");
        model.setExtUserInfo(extUserInfo);

        request.setBizModel(model);
        // 第三方代调用模式下请设置app_auth_token
        // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");

        AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "POST");
        // 如果需要返回GET请求，请使用
        // AlipayTradeWapPayResponse response = alipayClient.pageExecute(request, "GET");
        String pageRedirectionData = response.getBody();
        System.out.println(pageRedirectionData);

        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
            // sdk版本是"4.38.0.ALL"及以上,可以参考下面的示例获取诊断链接
            // String diagnosisUrl = DiagnosisUtils.getDiagnosisUrl(response);
            // System.out.println(diagnosisUrl);
        }
    }
    private static AlipayConfig getAlipayConfig() {
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCIRuuGuuvPVwWbhMFW4WD44mtu9ctG/U/iP6BOUiM0qxtWZxUxjy0pDaWHAaiga7CBtVFUDvo7NYH3u8M/Wo2nGgAt45VXHe4XbsrdBC6GN1jEjKDRe+wJ0G8HinET7nxeVYT3tItkA62IgWN3V9br9LL8lL+gtbM9w+jS1wuTaGyjvGfQ+Ve7vFZwNyVL1sh2DDmzkskpvsA8hZlPs5F3TYsEZC39sdUUoAP9Dn7Q3H7fXPWUHketjNhQd590pUghRFmp8Pl504UW9scCdgE2yQd3l078/iizIjPKRrD11K3IUQUkT0D06rELewFx/N8b0rykjLJqZIxiu5Ryl7v3AgMBAAECggEANqirYxip6DmxKBVxLpWrPWgjSxgO9mQ3nDmE5KURNdjDWD50Q7J3nJW6pJHHvsAyxXOiMSLovsLRZDnNMCXz1ugY1k8H4S9CBegMOeWpe2/LjNoSzrLzk2QkhFPE0we+nl+Su3+QwQawnaW37XAv5ef5ulE3IxB92fIv+hECtZaA1n5giNAaXMO7Jq9fY/YkeBshXq5aor+hr8k2XpWFsHH13hQ2e3Opcx4nCYOf92A9Z5VVJxcm/uwqX6d6855QblSY3GGpAcml5VwRDV5IBbIbAfgtk1NSHTTqWIM1k+RSiG/E3UKMoLNcCFx2Aadygrql82aeUviaYmlqqSJGAQKBgQC8HfY/XNLWEMTRH+dAKXXg0kdeJcmqQ42InM4KK28nI98V06FBEdw8E4Q3WrmYuu7RdCvOdOAn4I41mEVvTChk8cnV/Q2Y6otduyUQWc+oSI3oSrfkJOMupW5EoVckp2vbfqc3ayroxeOLao3PQ//FqP1gtG7k7uoYg46Lj7X3gQKBgQC5dAmFx+tXGY/08DjGHLwE2rhvjMaEbXH6QRV7FM1/yoETZAvdhHGHuD690IbxZIU1g3lU0DyapekPkIpIDAmdqXqjnBzN7y91arhjVlKUBveBscyCNAN447K7HsDHyBIA7icI6C/qX68oUb5UCAgBaTMyn+F7WO9JyhBLTREvdwKBgQCtqKsuiy5HV2uZ8m20mZGsXfJsQ04L1BjQSJEcakROSZsjtx5hx28cNpgT0bkL+WWGDkxyZwqXRYGCkHNo71FK/rpoxRbMV5kYii8JDR/aJkB6dJ+WCUeARE+rI5YizDuRgTY0vsX55NTOWytWb1uqyPo/T9IfnT6WKrxosd42AQKBgFIVIO3cmbZ4cvYXqRlOuI1xfXaqGWedxXfdfLN4dqhcKhs6CeJm75UBoIuFE2Ued7NC2N4IOPq9+lTQRdQzt8HXtJdbzof7mMbgkHIvT+eC2ePGnVz3xp2oMoCN0Qfb7tQIlLBljGexKCaxX4tz7o8pT+bVSwi8SVUgoWui1btrAoGATJpj6YAnwYIYLMfq63wFDYUFH6ej7MDAM8hv3ZvRljKD9EGsr+TFT2g0V3w1nISPwofF+rDnYvybzRV3alcHP2Y+WRv+04VdCNcDPFdXYMVtsrtAXo1kNTizlHFYp5K5ZXiKKXsPnFv88FcV+UzMI7VJQ4M6+IpgKya6IkNEv2M=";
        String alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxN4bva1/PEO4+Mjtsn+fGeSqk+L12kYueV8rnYkBlMHnkypnDqkDi6Jc+pr/zAIoAoiOFSAeeVVhaejZOlFmspEOoH3XEtrxzKFqOaqcbFMxJI3PxuDljaFpL1zvK4UrFACw8Hxoa+KjbheW4kHT6ROx82XaCaiDtvBBV2vXdTiUyFVOip1XGzafyqHNk7g6Q44Y91o9xSE3boJJleipmK19Q3LVsZuT3Nk+SELppiZAla+9voGaeFzCDEisqVxKFCISidxg79HeQjaIkYmGBxFFY2ow5gsPgQAfM2svJuhoimVXbDc95n1gHfVgWfCSdjeqE2+noYtkCMol2hoCbQIDAQAB";
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
        alipayConfig.setAppId("2021000143601715");
        alipayConfig.setPrivateKey(privateKey);
        alipayConfig.setFormat("json");
        alipayConfig.setAlipayPublicKey(alipayPublicKey);
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        return alipayConfig;
    }
}