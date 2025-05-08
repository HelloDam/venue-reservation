package com.vrs.service.geo;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 根据经纬度逆向解析
 *
 * @Author dam
 * @create 2025/4/15 11:39
 */
@Component
public class AMapGeoApi {

    @Value("${vrs.map.amap.key}")
    private String key;

    /**
     * 根据经纬度获取详细地址
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @return 格式化地址（如 "北京市海淀区燕园街道北京大学"）
     * @throws RuntimeException 如果API请求失败
     */
    public String getAddressByLocation(BigDecimal longitude, BigDecimal latitude) {
        String url = String.format(
                "https://restapi.amap.com/v3/geocode/regeo?location=%s,%s&key=%s",
                longitude,
                latitude,
                key
        );

        String response = HttpUtil.get(url);
        JSONObject json = JSONUtil.parseObj(response);

        if (!"1".equals(json.getStr("status"))) {
            throw new RuntimeException("高德逆地理编码失败: " + json.getStr("info"));
        }

        return json.getByPath("regeocode.formatted_address", String.class);
    }
}