//package com.vrs;
//
//import com.alipay.v3.ApiClient;
//import com.alipay.v3.ApiException;
//import com.alipay.v3.Configuration;
//import com.alipay.v3.api.AlipayTradeApi;
//import com.alipay.v3.model.AlipayTradeRefundDefaultResponse;
//import com.alipay.v3.model.AlipayTradeRefundModel;
//import com.alipay.v3.model.AlipayTradeRefundResponseModel;
//import com.alipay.v3.util.model.AlipayConfig;
//import com.alipay.v3.util.model.CustomizedParams;
//
///**
// * @Author dam
// * @create 2025/1/2 19:29
// */
//public class AlipayTradeApiRefundV3 {
//    public static void main(String[] args) throws ApiException {
//        ApiClient defaultClient = Configuration.getDefaultApiClient();
//        // 初始化alipay参数（全局设置一次）
//        defaultClient.setAlipayConfig(getAlipayConfig());
//
//        // 构造请求参数以调用接口
//        AlipayTradeApi api = new AlipayTradeApi();
//        String targetAppId = null;
//        AlipayTradeRefundModel data = new AlipayTradeRefundModel();
//
//        // 设置商户订单号
//        data.setOutTradeNo("1874780217354649600850432");
//
//        // 设置支付宝交易号
////        data.setTradeNo("2014112611001004680073956707");
//
//        // 设置退款金额
//        data.setRefundAmount("50.0");
//
//        // 设置退款原因说明
//        data.setRefundReason("正常退款");
//
//        // 设置退款请求号
////        data.setOutRequestNo("HZ01RF001");
//
//        // 设置退款包含的商品列表信息
////
//
//
//        // 设置退分账明细信息
////        List<OpenApiRoyaltyDetailInfoPojo> refundRoyaltyParameters = new ArrayList<OpenApiRoyaltyDetailInfoPojo>();
////        OpenApiRoyaltyDetailInfoPojo refundRoyaltyParameters0 = new OpenApiRoyaltyDetailInfoPojo();
////        refundRoyaltyParameters0.setAmount("0.1");
////        refundRoyaltyParameters0.setTransIn("2088101126708402");
////        refundRoyaltyParameters0.setRoyaltyType("transfer");
////        refundRoyaltyParameters0.setTransOut("2088101126765726");
////        refundRoyaltyParameters0.setTransOutType("userId");
////        refundRoyaltyParameters0.setRoyaltyScene("达人佣金");
////        refundRoyaltyParameters0.setTransInType("userId");
////        refundRoyaltyParameters0.setTransInName("张三");
////        refundRoyaltyParameters0.setDesc("分账给2088101126708402");
////        refundRoyaltyParameters.add(refundRoyaltyParameters0);
////        data.setRefundRoyaltyParameters(refundRoyaltyParameters);
//
//        // 设置查询选项
////        List<String> queryOptions = new ArrayList<String>();
////        queryOptions.add("refund_detail_item_list");
////        data.setQueryOptions(queryOptions);
//
//        // 设置针对账期交易
////        data.setRelatedSettleConfirmNo("2024041122001495000530302869");
//
//
//        // 第三方代调用模式下请设置app_auth_token
//        CustomizedParams params = new CustomizedParams();
////        params.setAppAuthToken("<-- 请填写应用授权令牌 -->");
//
//        try {
//            AlipayTradeRefundResponseModel response = api.refund(data, params);
//        } catch (ApiException e) {
//            AlipayTradeRefundDefaultResponse errorObject = (AlipayTradeRefundDefaultResponse) e.getErrorObject();
//            System.out.println("调用失败:" + errorObject);
//        }
//    }
//
//    private static AlipayConfig getAlipayConfig() {
//        AlipayConfig alipayConfig = new AlipayConfig();
//        alipayConfig.setServerUrl("https://openapi-sandbox.dl.alipaydev.com/gateway.do");
//        alipayConfig.setAppId("2021000143601715");
//        alipayConfig.setPrivateKey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCIRuuGuuvPVwWbhMFW4WD44mtu9ctG/U/iP6BOUiM0qxtWZxUxjy0pDaWHAaiga7CBtVFUDvo7NYH3u8M/Wo2nGgAt45VXHe4XbsrdBC6GN1jEjKDRe+wJ0G8HinET7nxeVYT3tItkA62IgWN3V9br9LL8lL+gtbM9w+jS1wuTaGyjvGfQ+Ve7vFZwNyVL1sh2DDmzkskpvsA8hZlPs5F3TYsEZC39sdUUoAP9Dn7Q3H7fXPWUHketjNhQd590pUghRFmp8Pl504UW9scCdgE2yQd3l078/iizIjPKRrD11K3IUQUkT0D06rELewFx/N8b0rykjLJqZIxiu5Ryl7v3AgMBAAECggEANqirYxip6DmxKBVxLpWrPWgjSxgO9mQ3nDmE5KURNdjDWD50Q7J3nJW6pJHHvsAyxXOiMSLovsLRZDnNMCXz1ugY1k8H4S9CBegMOeWpe2/LjNoSzrLzk2QkhFPE0we+nl+Su3+QwQawnaW37XAv5ef5ulE3IxB92fIv+hECtZaA1n5giNAaXMO7Jq9fY/YkeBshXq5aor+hr8k2XpWFsHH13hQ2e3Opcx4nCYOf92A9Z5VVJxcm/uwqX6d6855QblSY3GGpAcml5VwRDV5IBbIbAfgtk1NSHTTqWIM1k+RSiG/E3UKMoLNcCFx2Aadygrql82aeUviaYmlqqSJGAQKBgQC8HfY/XNLWEMTRH+dAKXXg0kdeJcmqQ42InM4KK28nI98V06FBEdw8E4Q3WrmYuu7RdCvOdOAn4I41mEVvTChk8cnV/Q2Y6otduyUQWc+oSI3oSrfkJOMupW5EoVckp2vbfqc3ayroxeOLao3PQ//FqP1gtG7k7uoYg46Lj7X3gQKBgQC5dAmFx+tXGY/08DjGHLwE2rhvjMaEbXH6QRV7FM1/yoETZAvdhHGHuD690IbxZIU1g3lU0DyapekPkIpIDAmdqXqjnBzN7y91arhjVlKUBveBscyCNAN447K7HsDHyBIA7icI6C/qX68oUb5UCAgBaTMyn+F7WO9JyhBLTREvdwKBgQCtqKsuiy5HV2uZ8m20mZGsXfJsQ04L1BjQSJEcakROSZsjtx5hx28cNpgT0bkL+WWGDkxyZwqXRYGCkHNo71FK/rpoxRbMV5kYii8JDR/aJkB6dJ+WCUeARE+rI5YizDuRgTY0vsX55NTOWytWb1uqyPo/T9IfnT6WKrxosd42AQKBgFIVIO3cmbZ4cvYXqRlOuI1xfXaqGWedxXfdfLN4dqhcKhs6CeJm75UBoIuFE2Ued7NC2N4IOPq9+lTQRdQzt8HXtJdbzof7mMbgkHIvT+eC2ePGnVz3xp2oMoCN0Qfb7tQIlLBljGexKCaxX4tz7o8pT+bVSwi8SVUgoWui1btrAoGATJpj6YAnwYIYLMfq63wFDYUFH6ej7MDAM8hv3ZvRljKD9EGsr+TFT2g0V3w1nISPwofF+rDnYvybzRV3alcHP2Y+WRv+04VdCNcDPFdXYMVtsrtAXo1kNTizlHFYp5K5ZXiKKXsPnFv88FcV+UzMI7VJQ4M6+IpgKya6IkNEv2M=");
//        alipayConfig.setAlipayPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxN4bva1/PEO4+Mjtsn+fGeSqk+L12kYueV8rnYkBlMHnkypnDqkDi6Jc+pr/zAIoAoiOFSAeeVVhaejZOlFmspEOoH3XEtrxzKFqOaqcbFMxJI3PxuDljaFpL1zvK4UrFACw8Hxoa+KjbheW4kHT6ROx82XaCaiDtvBBV2vXdTiUyFVOip1XGzafyqHNk7g6Q44Y91o9xSE3boJJleipmK19Q3LVsZuT3Nk+SELppiZAla+9voGaeFzCDEisqVxKFCISidxg79HeQjaIkYmGBxFFY2ow5gsPgQAfM2svJuhoimVXbDc95n1gHfVgWfCSdjeqE2+noYtkCMol2hoCbQIDAQAB");
//        return alipayConfig;
//    }
//}
