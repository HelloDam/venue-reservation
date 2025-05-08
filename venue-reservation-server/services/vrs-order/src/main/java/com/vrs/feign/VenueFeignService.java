package com.vrs.feign;

import com.vrs.convention.result.Result;
import com.vrs.domain.dto.req.OrderDetailReqDTO;
import com.vrs.domain.dto.req.OrderListDetailReqDTO;
import com.vrs.domain.dto.req.TimePeriodStockRestoreReqDTO;
import com.vrs.domain.dto.resp.OrderDetailRespDTO;
import com.vrs.domain.dto.resp.OrderListDetailRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author dam
 * @create 2024/12/1 16:19
 */
@FeignClient(value = "vrs-venue", url = "${aggregation.remote-url:}")
public interface VenueFeignService {

    /**
     * 时间段库存回退（包括数据库和缓存）
     */
    @PostMapping("/venue/time-period/v1/release")
    public Result release(@Validated @RequestBody TimePeriodStockRestoreReqDTO timePeriodStockRestoreReqDTO);

    /**
     * 获取订单详情
     */
    @PostMapping("/venue/forOrder/getOrderDetail")
    public Result<OrderDetailRespDTO> getOrderDetail(@Validated @RequestBody OrderDetailReqDTO orderDetailReqDTO);

    /**
     * 获取订单详情（集合）
     */
    @PostMapping("/venue/forOrder/getOrderListDetail")
    public Result<OrderListDetailRespDTO> getOrderListDetail(@Validated @RequestBody OrderListDetailReqDTO orderListDetailReqDTO);

}
