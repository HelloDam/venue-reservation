package com.vrs.domain.dto.req;

import com.vrs.convention.page.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author dam
 * @create 2024/12/7 10:51
 */
@Data
public class OrderListReqDTO extends PageRequest {
    /**
     * 下单用户id
     */
    @Schema(description = "userId", hidden = true)
    private Long userId;

    /**
     * 订单状态 0:未支付 1：已支付 2：取消 3：退款
     */
    private Integer orderStatus;
}
