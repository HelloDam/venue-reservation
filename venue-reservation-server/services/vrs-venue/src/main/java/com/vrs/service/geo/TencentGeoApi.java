package com.vrs.service.geo;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 根据经纬度逆向解析（腾讯地图版）
 *
 * @Author dam
 * @create 2025/4/15 11:39
 */
@Component
public class TencentGeoApi {

    @Value("${vrs.map.tencent.key}")
    private String key;

    /**
     * 根据经纬度获取详细地址
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return 推荐地址（如 "海淀区中关村中国技术交易大厦(海淀桥东南200米)"）
     * @throws RuntimeException 如果API请求失败
     */
    public String getAddressByLocation(BigDecimal longitude, BigDecimal latitude) {
        String url = String.format(
                "https://apis.map.qq.com/ws/geocoder/v1/?location=%s,%s&key=%s&get_poi=0",
                latitude,
                longitude,
                key
        );

        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);

        if (json.getInt("status") != 0) {
            throw new RuntimeException("腾讯逆地理编码失败: " + json.getStr("message"));
        }
        String location = json.getByPath("result.formatted_addresses.recommend", String.class);
        return location == null ? "未知地址" : location;
    }
}