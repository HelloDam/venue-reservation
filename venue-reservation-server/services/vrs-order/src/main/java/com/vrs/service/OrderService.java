package com.vrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrs.convention.page.PageResponse;
import com.vrs.domain.dto.mq.ExecuteReserveMqDTO;
import com.vrs.domain.dto.req.OrderGenerateReqDTO;
import com.vrs.domain.dto.req.OrderListReqDTO;
import com.vrs.domain.dto.resp.AlipayInfoRespDTO;
import com.vrs.domain.dto.resp.AlipayRefundRespDTO;
import com.vrs.domain.dto.resp.OrderRespDTO;
import com.vrs.domain.entity.OrderDO;

/**
 * @author dam
 * @description 针对表【order】的数据库操作Service
 * @createDate 2024-11-30 19:03:04
 */
public interface OrderService extends IService<OrderDO> {

    OrderDO generateOrder(OrderGenerateReqDTO orderGenerateReqDTO);

    void closeOrder(String orderSn);

    void secondCloseOrder(String orderSn);

    PageResponse<OrderRespDTO> pageOrderDO(OrderListReqDTO request);

    String generateORCode(String orderSn);

    String pay(String orderSn, String returnUrl);

    void payOrder(String orderSn);

    AlipayRefundRespDTO refund(String orderSn);

    void refundOrder(String orderSn);

    /**
     * 获取订单详细信息
     *
     * @param orderSn
     * @return
     */
    OrderRespDTO getOrderRespDTOByOrderSn(String orderSn);

    AlipayInfoRespDTO info(String orderSn);

    void generateOrder(ExecuteReserveMqDTO message);

    OrderRespDTO getNearestOrder(Long userId);
}
