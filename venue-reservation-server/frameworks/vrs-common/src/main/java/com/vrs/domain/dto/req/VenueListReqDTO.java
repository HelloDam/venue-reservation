package com.vrs.domain.dto.req;

import com.vrs.convention.page.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author dam
 * @create 2024/12/7 10:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueListReqDTO extends PageRequest {
    /**
     * 场馆名称
     */
    private String name;

    /**
     * 场馆类型 1:篮球馆（场） 2:足球场 3：羽毛球馆（场） 4:排球馆（场）100：体育馆 1000:其他
     */
    private Integer type;

    /**
     * 维度
     */
    private BigDecimal latitude;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 多少千米
     */
    private double km;

    /**
     * 场馆状态 0：关闭 1：开放 2：维护中
     */
    private Integer status;
}
