package com.vrs.service.chainHander.canal;

import cn.hutool.core.util.ObjectUtil;
import com.vrs.chain_of_responsibility.AbstractChainHandler;
import com.vrs.constant.ChainConstant;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.domain.dto.mq.CanalBinlogDTO;
import com.vrs.service.PartitionService;
import com.vrs.service.TimePeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 时间段库存修改处理逻辑
 *
 * @Author dam
 * @create 2024/12/11 19:43
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TimePeriodStockChangeHandler implements AbstractChainHandler<CanalBinlogDTO> {

    private final StringRedisTemplate redisTemplate;
    private final TimePeriodService timePeriodService;
    private final PartitionService partitionService;

    @Override
    public boolean handle(CanalBinlogDTO canalBinlogDTO) {
        Map<String, Object> alterDataMap = canalBinlogDTO.getData().get(0);
        Map<String, Object> oldDataMap = canalBinlogDTO.getOld().get(0);
        if (ObjectUtil.equal(canalBinlogDTO.getType(), "UPDATE") && oldDataMap.containsKey("stock")) {
            // --if-- 如果是修改操作，且修改了stock
            log.info("[消费者] 消费canal的消息，时间段库存修改，同步修改缓存的库存，时间段ID：{}", alterDataMap.get("id"));
            Long timePeriodId = Long.parseLong(alterDataMap.get("id").toString());
            Long partitionId = Long.parseLong(alterDataMap.get("partition_id").toString());
            Integer stock = Integer.parseInt(alterDataMap.get("stock").toString());
            Long bookedSlots = Long.parseLong(alterDataMap.get("booked_slots").toString());
            // 更新库存
            redisTemplate.opsForValue().set(String.format(RedisCacheConstant.VENUE_TIME_PERIOD_STOCK_KEY, timePeriodId), stock.toString());
            // 更新位图
            timePeriodService.initializeFreeIndexBitmap(
                    String.format(RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY, timePeriodId),
                    partitionService.getPartitionDOById(partitionId).getNum(),
                    bookedSlots,
                    24 * 3600);
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
        return 10;
    }
}
