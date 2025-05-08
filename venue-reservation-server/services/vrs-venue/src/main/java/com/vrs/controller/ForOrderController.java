package com.vrs.controller;

import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.OrderDetailReqDTO;
import com.vrs.domain.dto.req.OrderListDetailReqDTO;
import com.vrs.domain.dto.resp.OrderDetailRespDTO;
import com.vrs.domain.dto.resp.OrderListDetailRespDTO;
import com.vrs.service.ForOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 查询订单详情
 */
@RestController
@RequestMapping("/venue/forOrder/")
@RequiredArgsConstructor
@Tag(name = "场馆——供订单服务调用")
public class ForOrderController {

    private final ForOrderService forOrderService;

    /**
     * 获取订单详情
     */
    @PostMapping("/getOrderDetail")
    @Operation(summary = "获取订单详情信息")
    public Result<OrderDetailRespDTO> getOrderDetail(@Validated @RequestBody OrderDetailReqDTO orderDetailReqDTO) {
        OrderDetailRespDTO orderDetailRespDTO = forOrderService.getOrderDetail(orderDetailReqDTO);
        return Results.success(orderDetailRespDTO);
    }

    /**
     * 获取订单详情（集合）
     */
    @PostMapping("/getOrderListDetail")
    @Operation(summary = "获取订单详情信息（集合）")
    public Result<OrderListDetailRespDTO> getOrderDetail(@Validated @RequestBody OrderListDetailReqDTO orderListDetailReqDTO) {
        OrderListDetailRespDTO orderListDetailRespDTO = forOrderService.getOrderListDetail(orderListDetailReqDTO);
        return Results.success(orderListDetailRespDTO);
    }

}
