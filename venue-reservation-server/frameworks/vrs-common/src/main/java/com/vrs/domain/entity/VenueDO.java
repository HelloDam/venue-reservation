package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import com.vrs.domain.validate.AddGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * @TableName venue
 */
@TableName(value = "venue")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VenueDO extends BaseEntity implements Serializable {

    /**
     * 所属机构ID
     */
    @NotNull(message = "机构Id不能为空", groups = {AddGroup.class})
    private Long organizationId;

    /**
     * 场馆名称
     */
    @NotBlank(message = "场馆名称不能为空", groups = {AddGroup.class})
    private String name;

    /**
     * 场馆类型 1:篮球馆（场） 2:足球场 3：羽毛球馆（场） 4:排球馆（场）100：体育馆 1000:其他
     */
    @NotNull(message = "场馆类型不能为空", groups = {AddGroup.class})
    private Integer type;

    /**
     * 场馆地址
     */
    @NotBlank(message = "场馆地址不能为空", groups = {AddGroup.class})
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
    @NotNull(message = "场馆状态不能为空", groups = {AddGroup.class})
    private Integer status;

    /**
     * 是否对外开放 0：否 1：是 如果不对外开放，需要相同机构的用户才可以预定
     */
    @NotNull(message = "是否对外开放不能为空", groups = {AddGroup.class})
    private Integer isOpen;

    /**
     * 提前可预定天数，例如设置为1，即今天可预订明天的场
     */
    @NotNull(message = "提前可预定天数不能为空", groups = {AddGroup.class})
    private Integer advanceBookingDay;

    /**
     * 开放预订时间
     */
    @NotNull(message = "开放预订时间不能为空", groups = {AddGroup.class})
    private LocalTime startBookingTime;

    /**
     * 维度
     */
    @NotNull(message = "纬度不能为空", groups = {AddGroup.class})
    private BigDecimal latitude;

    /**
     * 经度
     */
    @NotNull(message = "经度不能为空", groups = {AddGroup.class})
    private BigDecimal longitude;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
