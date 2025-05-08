package com.vrs.service.chainHander.canal;

import cn.hutool.core.util.ObjectUtil;
import com.vrs.chain_of_responsibility.AbstractChainHandler;
import com.vrs.constant.ChainConstant;
import com.vrs.constant.OrderStatusConstant;
import com.vrs.domain.dto.mq.CanalBinlogDTO;
import com.vrs.domain.dto.req.TimePeriodStockRestoreReqDTO;
import com.vrs.service.TimePeriodService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 订单超时关闭处理逻辑
 *
 * @Author dam
 * @create 2024/12/11 19:43
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCloseHandler implements AbstractChainHandler<CanalBinlogDTO> {

    private final TimePeriodService timePeriodService;

    @Override
    public boolean handle(CanalBinlogDTO canalBinlogDTO) {
        Map<String, Object> alterDataMap = canalBinlogDTO.getData().get(0);
        Map<String, Object> oldDataMap = canalBinlogDTO.getOld().get(0);
        if (ObjectUtil.equal(canalBinlogDTO.getType(), "UPDATE") && oldDataMap.containsKey("order_status")) {
            log.info("[消费者] 消费canal的消息，订单超时关闭，恢复时间段的库存和空闲场号，时间段ID：{}", alterDataMap.get("time_period_id"));
            Long userId = Long.parseLong(alterDataMap.get("user_id").toString());
            Long timePeriodId = Long.parseLong(alterDataMap.get("time_period_id").toString());
            Long partitionId = Long.parseLong(alterDataMap.get("partition_id").toString());
            Long courtIndex;
            if (alterDataMap.containsKey("partition_index")) {
                courtIndex = Long.parseLong(alterDataMap.get("partition_index").toString());
            } else {
                courtIndex = Long.parseLong(alterDataMap.get("court_index").toString());
            }
            Integer orderStatus = Integer.parseInt(alterDataMap.get("order_status").toString());
            Integer oldOrderStatus = Integer.parseInt(oldDataMap.get("order_status").toString());
            if (orderStatus.equals(OrderStatusConstant.CANCEL) && oldOrderStatus.equals(OrderStatusConstant.UN_PAID)) {
                // 恢复库存
                timePeriodService.restoreStockAndBookedSlots(TimePeriodStockRestoreReqDTO.builder()
                        .userId(userId)
                        .courtIndex(courtIndex)
                        .timePeriodId(timePeriodId)
                        .partitionId(partitionId)
                        .build());
            }
            return true;
        }
        return false;
    }

    @Override
    public String name() {
        return ChainConstant.CANAL_CHAIN_NAME;
    }

    @Override
    public int order() {
        return 0;
    }
}
