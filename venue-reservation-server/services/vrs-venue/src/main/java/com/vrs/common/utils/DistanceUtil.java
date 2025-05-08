package com.vrs.common.utils;

/**
 * 根据经纬度结算公里
 * @Author dam
 * @create 2025/1/28 19:39
 */
public class DistanceUtil {
    /**
     * 地球半径，单位：公里
     */
    private static final double EARTH_RADIUS = 6371;

    /**
     * 计算两个经纬度点之间的距离（公里）
     *
     * @param lat1 纬度 1
     * @param lon1 经度 1
     * @param lat2 纬度 2
     * @param lon2 经度 2
     * @return 距离（公里）
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
