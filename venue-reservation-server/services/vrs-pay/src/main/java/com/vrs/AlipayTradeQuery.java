package com.vrs;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.diagnosis.DiagnosisUtils;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author dam
 * @create 2025/1/3 9:33
 */
public class AlipayTradeQuery {

    public static void main(String[] args) throws AlipayApiException {
        // 初始化SDK
        AlipayClient alipayClient = new DefaultAlipayClient(getAlipayConfig());

        // 构造请求参数以调用接口
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        // 设置订单支付时传入的商户订单号
        model.setOutTradeNo("1874780217354649600850432");

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
