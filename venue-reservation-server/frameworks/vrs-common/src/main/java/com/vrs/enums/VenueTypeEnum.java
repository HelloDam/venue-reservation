package com.vrs.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场馆类型枚举
 */
@RequiredArgsConstructor
public enum VenueTypeEnum {

    BASKET_BALL(0, "篮球", "&#xe77d;", "#cc9e60"),
    FOOT_BALL(1, "足球", "&#xe781;", "#010102"),
    BADMINTON(2, "羽毛球", "&#xe773;", "#80b438"),
    VOLLEYBALL(3, "排球", "&#xe774;", ""),
    TABLE_TENNIS(4, "乒乓球", "&#xe79e;", "#54b4bc"),
    TENNIS(5, "网球", "&#xe79f;", "#4096e4"),
    SWIMMING(6, "游泳", "&#xe619;", ""),
    FITNESS_CENTER(7, "健身房", "&#xe82d;", ""),
    HANDBALL(8, "手球", "&#xe641;", ""),
    ICE_SKATING(9, "滑冰", "&#xe653;", ""),
    SKATEBOARDING(10, "滑板", "&#xe655;", ""),
    CLIMBING(11, "攀岩", "&#xe647;", ""),
    ARCHERY(12, "射箭", "&#xe775;", ""),
    BOXING(13, "拳击", "&#xe77f;", ""),
    EQUESTRIAN(14, "马术", "&#xe646;", ""),
    RUGBY(15, "橄榄球", "&#xe77b;", ""),
    HOCKEY(16, "曲棍球", "&#xe643;", ""),
    SYNTHESIS(17, "综合型", "&#xe7a1;", "");

    @Getter
    private final int type;

    @Getter
    private final String value;

    @Getter
    private final String icon;

    @Getter
    private final String color;

    /**
     * 根据 type 找到对应的 value
     *
     * @param type 要查找的类型代码
     * @return 对应的描述值，如果没有找到抛异常
     */
    public static String findValueByType(int type) {
        for (VenueTypeEnum target : VenueTypeEnum.values()) {
            if (target.getType() == type) {
                return target.getValue();
            }
        }
        return "未知类型";
    }


    /**
     * 根据关键字找到所有包含关键字的枚举实例，并返回它们的type和value
     *
     * @param keyword 要查找的关键字
     * @return 包含关键字的所有枚举实例的type和value的列表
     */
    public static List<Map<String, Object>> findEnumsInfoByKeyword(String keyword) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (VenueTypeEnum venueType : values()) {
            if (venueType.getValue().toLowerCase().contains(keyword.toLowerCase())) {
                Map<String, Object> info = new HashMap<>();
                info.put("type", venueType.getType());
                info.put("value", venueType.getValue());
                info.put("icon", venueType.getIcon().equals("") ? "ue694" : venueType.getIcon());
                info.put("color", venueType.getColor().equals("") ? "#fc8834" : venueType.getColor());
                result.add(info);
            }
        }
        return result;
    }

    /**
     * 获取所有场馆类型的type值
     *
     * @return 包含所有type值的列表
     */
    public static List<Integer> getAllTypeCode() {
        List<Integer> types = new ArrayList<>();
        for (VenueTypeEnum venueType : values()) {
            types.add(venueType.getType());
        }
        return types;
    }

    /**
     * 获取所有场馆类型的type值
     *
     * @return 包含所有type值的列表
     */
    public static VenueTypeEnum[] getAllTypes() {
        return values();
    }
}
