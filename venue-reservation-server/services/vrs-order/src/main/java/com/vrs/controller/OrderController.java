package com.vrs.controller;

import com.vrs.annotation.Idempotent;
import com.vrs.common.context.UserContext;
import com.vrs.convention.page.PageResponse;
import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.OrderGenerateReqDTO;
import com.vrs.domain.dto.req.OrderListReqDTO;
import com.vrs.domain.dto.resp.AlipayRefundRespDTO;
import com.vrs.domain.dto.resp.OrderRespDTO;
import com.vrs.domain.entity.OrderDO;
import com.vrs.domain.validate.UpdateGroup;
import com.vrs.enums.IdempotentSceneEnum;
import com.vrs.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "订单相关")
@RequestMapping("/order/")
public class OrderController {

    private final OrderService orderService;

    /**
     * 生成订单
     */
    @PostMapping("/v1/generateOrder")
    @Operation(summary = "生成订单")
    public Result<OrderDO> generateOrder(@RequestBody OrderGenerateReqDTO orderGenerateReqDTO) {
        OrderDO orderDO = orderService.generateOrder(orderGenerateReqDTO);
        return Results.success(orderDO);
    }

    /**
     * 获取最近的已支付订单
     */
    @PostMapping("/v1/getNearestOrder")
    @Operation(summary = "获取最近的已支付订单")
    public Result<OrderRespDTO> getNearestOrder() {
        Long userId = UserContext.getUserId();
        OrderRespDTO orderDO = orderService.getNearestOrder(userId);
        return Results.success(orderDO);
    }

    /**
     * 查询自己的订单列表
     */
    @PostMapping("/listMyOrder")
    @Operation(summary = "查询自己的订单列表")
    public Result<PageResponse<OrderRespDTO>> listMyOrder(@RequestBody OrderListReqDTO request) {
        request.setUserId(UserContext.getUserId());
        PageResponse<OrderRespDTO> list = orderService.pageOrderDO(request);
        return Results.success(list);
    }

    /**
     * 查询数据
     */
    @PostMapping("/list")
    @Operation(summary = "查询订单列表")
    public Result<PageResponse<OrderRespDTO>> list(@RequestBody OrderListReqDTO request) {
        PageResponse<OrderRespDTO> list = orderService.pageOrderDO(request);
        return Results.success(list);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/removeById")
    @Operation(summary = "根据ID删除订单")
    public Result removeById(Integer id) {
        orderService.removeById(id);
        return Results.success();
    }

    /**
     * 删除数据
     */
    @PostMapping("/removeByIds")
    @Operation(summary = "根据ID集合批量删除订单")
    public Result removeByIds(@RequestBody List<Long> idList) {
        orderService.removeByIds(idList);
        return Results.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    @Operation(summary = "修改订单")
    public Result update(@Validated({UpdateGroup.class}) @RequestBody OrderDO orderDO) {
        orderService.updateById(orderDO);
        return Results.success();
    }

    /**
     * 根据id获取订单
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @Operation(summary = "根据ID获取订单")
    public Result getById(@PathVariable Long id) {
        return Results.success(orderService.getById(id));
    }

    /**
     * 根据订单号获取订单
     *
     * @param orderSn
     * @return
     */
    @GetMapping("/getByOrderSn")
    @Operation(summary = "根据订单号获取订单")
    public Result getOrderRespDTOByOrderSn(@RequestParam("orderSn") String orderSn) {
        return Results.success(orderService.getOrderRespDTOByOrderSn(orderSn));
    }

    /**
     * 生成二维码
     *
     * @param orderSn
     * @return
     */
    @GetMapping("/generateORCode")
    @Operation(summary = "根据订单ID获取二维码")
    public Result generateORCode(@RequestParam("orderSn") String orderSn) {
        return Results.success(orderService.generateORCode(orderSn));
    }

    /**
     * 订单支付
     *
     * @param orderSn
     * @return
     */
    @GetMapping("/pay")
    @Operation(summary = "订单支付")
    @Idempotent(
            uniqueKeyPrefix = "vrs-order:lock_pay:",
            key = "#orderSn",
            message = "正在执行订单支付流程，请勿重复进行...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    public Result pay(@RequestParam("orderSn") String orderSn, @RequestParam("returnUrl") String returnUrl) {
        return Results.success(orderService.pay(orderSn, returnUrl));
    }

    /**
     * 交易详情
     *
     * @param orderSn
     * @return
     */
    @GetMapping("/info")
    @Operation(summary = "查看交易详情")
    public Result info(@RequestParam("orderSn") String orderSn) {
        return Results.success(orderService.info(orderSn));
    }

    /**
     * 订单退款
     *
     * @param orderSn
     * @return
     */
    @GetMapping("/refund")
    @Operation(summary = "订单退款")
    @Idempotent(
            uniqueKeyPrefix = "vrs-order:lock_refund:",
            key = "#orderSn",
            message = "正在执行订单退款流程，请勿重复进行...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    public Result refund(@RequestParam("orderSn") String orderSn) {
        AlipayRefundRespDTO refundRespDTO = orderService.refund(orderSn);
        if (refundRespDTO.isSuccess()) {
            return Results.success();
        } else {
            return Results.failure(refundRespDTO.getCode(), "退款失败：" + refundRespDTO.getMsg() + "_" + refundRespDTO.getSubMsg());
        }
    }
}
