package com.vrs.service.impl;

import com.vrs.domain.dto.req.OrderDetailReqDTO;
import com.vrs.domain.dto.req.OrderListDetailReqDTO;
import com.vrs.domain.dto.resp.OrderDetailRespDTO;
import com.vrs.domain.dto.resp.OrderListDetailRespDTO;
import com.vrs.service.ForOrderService;
import com.vrs.service.PartitionService;
import com.vrs.service.TimePeriodService;
import com.vrs.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @Author dam
 * @create 2024/12/28 17:29
 */
@Service
@RequiredArgsConstructor
public class ForOrderServiceImpl implements ForOrderService {
    private final VenueService venueService;
    private final PartitionService partitionService;
    private final TimePeriodService timePeriodService;

    @Override
    public OrderDetailRespDTO getOrderDetail(OrderDetailReqDTO orderDetailReqDTO) {
        return OrderDetailRespDTO.builder()
                .partitionDO(partitionService.getPartitionDOById(orderDetailReqDTO.getPartitionId()))
                .venueDO(venueService.getVenueDOById(orderDetailReqDTO.getVenueId()))
                .build();
    }

    @Override
    public OrderListDetailRespDTO getOrderListDetail(OrderListDetailReqDTO orderListDetailReqDTO) {
        return OrderListDetailRespDTO.builder()
                .partitionDOList(partitionService.listPartitionWithIdList(orderListDetailReqDTO.getPartitionIdList(), orderListDetailReqDTO.getVenueIdList()))
                .venueDOList(venueService.listVenueWithIdList(orderListDetailReqDTO.getVenueIdList()))
                .build();
    }
}
