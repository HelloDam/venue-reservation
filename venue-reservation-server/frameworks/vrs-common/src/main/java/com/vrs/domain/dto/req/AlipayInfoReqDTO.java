package com.vrs.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dam
 * @create 2024/12/31 14:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlipayInfoReqDTO {
    /**
     * 订单号
     */
    private String orderSn;
}
