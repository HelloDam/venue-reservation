package com.vrs.utils;

import java.time.*;
import java.util.Date;

/**
 * @Author dam
 * @create 2024/12/7 16:37
 */
public class DateUtil {
    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalTime dateToLocalTime(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalTime();
    }

    public static Date combineLocalDateAndLocalTimeToDate(LocalDate localDate, LocalTime localTime) {
        // 组合 LocalDate 和 LocalTime 为 LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        // 将 LocalDateTime 转换为 Instant，指定时区
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();

        // 将 Instant 转换为 java.util.Date
        return Date.from(instant);
    }

    public static long combineLocalDateAndLocalTimeToDateTimeMill(LocalDate localDate, LocalTime localTime) {
        // 组合 LocalDate 和 LocalTime 为 LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        // 将 LocalDateTime 转换为 Instant，指定时区
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long localDateToTimestamp(LocalDate localDate) {
        // 将 LocalDate 转换为时间戳（毫秒数），设置时间为当天的00:00:00
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
