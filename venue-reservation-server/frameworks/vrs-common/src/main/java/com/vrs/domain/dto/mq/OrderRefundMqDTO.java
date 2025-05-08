package com.vrs.domain.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改订单为已退款状态
 * @Author dam
 * @create 2024/12/1 19:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRefundMqDTO {

    /**
     * 订单ID
     */
    private String orderSn;

}
