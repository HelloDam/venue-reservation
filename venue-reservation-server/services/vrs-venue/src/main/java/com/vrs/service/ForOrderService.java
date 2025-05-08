package com.vrs.service;

import com.vrs.domain.dto.req.OrderDetailReqDTO;
import com.vrs.domain.dto.req.OrderListDetailReqDTO;
import com.vrs.domain.dto.resp.OrderDetailRespDTO;
import com.vrs.domain.dto.resp.OrderListDetailRespDTO;

/**
 * @Author dam
 * @create 2024/12/28 17:29
 */
public interface ForOrderService {

    OrderDetailRespDTO getOrderDetail(OrderDetailReqDTO orderDetailReqDTO);

    OrderListDetailRespDTO getOrderListDetail(OrderListDetailReqDTO orderListDetailReqDTO);
}
