package com.vrs.feign;

import com.vrs.convention.result.Result;
import com.vrs.domain.dto.req.OrderGenerateReqDTO;
import com.vrs.domain.entity.OrderDO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author dam
 * @create 2024/12/1 16:19
 */
@FeignClient(value = "vrs-order", url = "${aggregation.remote-url:}")
public interface OrderFeignService {
    /**
     * 生成订单
     */
    @PostMapping("/order/v1/generateOrder")
    public Result<OrderDO> generateOrder(@RequestBody OrderGenerateReqDTO orderGenerateReqDTO);
}
