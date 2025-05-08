package com.vrs.domain.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 时间段库存和已预定场号更新
 * @Author dam
 * @create 2024/12/1 19:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimePeriodStockReduceMqDTO {

    /**
     * 订单 ID
     */
    private String orderSn;

    /**
     * 时间段ID
     */
    private Long partitionId;

    /**
     * 时间段ID
     */
    private Long timePeriodId;

    /**
     * 场号
     */
    private Long courtIndex;
}
