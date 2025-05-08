package com.vrs.domain.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * 
 * @TableName venue
 */
@TableName(value ="venue")
@Data
public class VenueRespDTO extends BaseEntity implements Serializable {

    /**
     * 所属机构ID
     */
    private Long organizationId;

    /**
     * 所属机构名称
     */
    private String organizationName;

    /**
     * 场馆名称
     */
    private String name;

    /**
     * 场馆类型 1:篮球馆（场） 2:足球场 3：羽毛球馆（场） 4:排球馆（场）100：体育馆 1000:其他
     */
    private Integer type;

    private String typeName;

    /**
     * 场馆地址
     */
    private String address;

    /**
     * 场馆描述，也可以说是否提供器材等等
     */
    private String description;

    /**
     * 场馆营业时间
     */
    private String openTime;

    /**
     * 联系电话
     */
    private String phoneNumber;

    /**
     * 场馆状态 0：关闭 1：开放 2：维护中
     */
    private Integer status;

    private String statusName;

    /**
     * 是否对外开放 0：否 1：是 如果不对外开放，需要相同机构的用户才可以预定
     */
    private Integer isOpen;

    /**
     * 提前可预定天数，例如设置为1，即今天可预订明天的场
     */
    private Integer advanceBookingDay;

    /**
     * 开放预订时间
     */
    private LocalTime startBookingTime;

    /**
     * 维度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 距离多少公里
     */
    private Double distance;

    /**
     * 场区图片
     */
    private List<PictureItemRespDTO> pictureList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}