package com.vrs.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class ChinaGeoLocation {
    // 中国地理边界
    private static final double MIN_LAT = 18.0;  // 最南端（曾母暗沙附近）
    private static final double MAX_LAT = 54.0;  // 最北端（漠河附近）
    private static final double MIN_LNG = 73.0;  // 最西端（帕米尔高原）
    private static final double MAX_LNG = 135.0; // 最东端（黑龙江与乌苏里江交汇处）

    // 地球半径(km)
    private static final double EARTH_RADIUS = 6371.0;

    /**
     * 生成中国范围内的随机经纬度
     *
     * @return 包含纬度和经度的数组 [latitude, longitude]
     */
    public static BigDecimal[] randomLocationInChina() {
        Random random = new Random();
        double latitude = MIN_LAT + (MAX_LAT - MIN_LAT) * random.nextDouble();
        double longitude = MIN_LNG + (MAX_LNG - MIN_LNG) * random.nextDouble();
        return new BigDecimal[]{new BigDecimal(latitude), new BigDecimal(longitude)};
    }

    /**
     * 在给定经纬度附近生成随机位置
     *
     * @param lat      中心点纬度
     * @param lng      中心点经度
     * @param radiusKm 半径(km)
     * @return 包含纬度和经度的数组 [latitude, longitude]
     */
    public static BigDecimal[] randomNearbyLocation(double lat, double lng, double radiusKm) {
        Random random = new Random();

        // 确保半径不超过中国边界
        radiusKm = Math.min(radiusKm, calculateMaxPossibleRadius(lat, lng));

        // 随机距离(0-radiusKm)
        double distance = radiusKm * Math.sqrt(random.nextDouble());

        // 随机方位角(0-2π)
        double bearing = random.nextDouble() * 2 * Math.PI;

        // 计算新坐标
        double newLat = Math.asin(Math.sin(Math.toRadians(lat)) * Math.cos(distance / EARTH_RADIUS) +
                Math.cos(Math.toRadians(lat)) * Math.sin(distance / EARTH_RADIUS) * Math.cos(bearing));

        double newLng = Math.toRadians(lng) + Math.atan2(Math.sin(bearing) * Math.sin(distance / EARTH_RADIUS) * Math.cos(Math.toRadians(lat)),
                Math.cos(distance / EARTH_RADIUS) - Math.sin(Math.toRadians(lat)) * Math.sin(newLat));

        newLat = Math.toDegrees(newLat);
        newLng = Math.toDegrees(newLng);

        // 确保结果在中国范围内
        newLat = Math.max(MIN_LAT, Math.min(MAX_LAT, newLat));
        newLng = Math.max(MIN_LNG, Math.min(MAX_LNG, newLng));

        // 保留6位小数
        return new BigDecimal[]{
                new BigDecimal(newLat).setScale(6, RoundingMode.HALF_UP),
                new BigDecimal(newLng).setScale(6, RoundingMode.HALF_UP)
        };
    }

    /**
     * 计算从给定点到中国边界最大可能半径
     */
    private static double calculateMaxPossibleRadius(double lat, double lng) {
        // 计算到四个边界的最小距离
        double toNorth = haversineDistance(lat, lng, MAX_LAT, lng);
        double toSouth = haversineDistance(lat, lng, MIN_LAT, lng);
        double toEast = haversineDistance(lat, lng, lat, MAX_LNG);
        double toWest = haversineDistance(lat, lng, lat, MIN_LNG);

        // 返回最小值
        return Math.min(Math.min(toNorth, toSouth), Math.min(toEast, toWest));
    }

    /**
     * 使用Haversine公式计算两点间距离(km)
     */
    private static double haversineDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}