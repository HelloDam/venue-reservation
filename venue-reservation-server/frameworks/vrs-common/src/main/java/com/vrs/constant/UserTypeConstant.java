package com.vrs.constant;

/**
 * 用户身份类型
 *
 * @author dam
 */
public class UserTypeConstant {

    /**
     * 系统管理员
     */
    public static final int SYSTEM_MANAGER = 0;

    /**
     * 机构管理员
     */
    public static final int INSTITUTE_MANAGER = 1;

    /**
     * 场馆管理员
     */
    public static final int VENUE_MANAGER = 2;

    /**
     * 普通用户
     */
    public static final int ORDINARY_USER = 100;

    /**
     * 校验用户是否为系统管理员
     *
     * @param userType
     */
    public static boolean validateSystemManager(int userType) {
        if (SYSTEM_MANAGER != userType) {
            return false;
        }
        return true;
    }

    /**
     * 校验用户是否为机构管理员
     *
     * @param userType
     */
    public static boolean validateInstituteManager(int userType) {
        if (INSTITUTE_MANAGER != userType) {
            return false;
        }
        return true;
    }

    /**
     * 校验用户是否为大于机构管理员的身份
     *
     * @param userType
     */
    public static boolean validateBiggerThanInstituteManager(int userType) {
        if (INSTITUTE_MANAGER < userType) {
            return false;
        }
        return true;
    }

    /**
     * 校验用户是否为大于场馆管理员的身份
     *
     * @param userType
     */
    public static boolean validateBiggerThanVenueManager(int userType) {
        if (VENUE_MANAGER < userType) {
            return false;
        }
        return true;
    }
}
