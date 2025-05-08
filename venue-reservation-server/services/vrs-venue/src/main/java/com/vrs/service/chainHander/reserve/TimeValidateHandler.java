package com.vrs.service.chainHander.reserve;

import com.vrs.chain_of_responsibility.AbstractChainHandler;
import com.vrs.constant.ChainConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ClientException;
import com.vrs.domain.dto.req.TimePeriodReserveReqDTO;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.domain.entity.VenueDO;
import com.vrs.service.PartitionService;
import com.vrs.service.VenueService;
import com.vrs.utils.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 校验时间是否正确
 * @Author dam
 * @create 2024/12/11 19:43
 */
@Component
@RequiredArgsConstructor
public class TimeValidateHandler implements AbstractChainHandler<TimePeriodReserveReqDTO> {

    private final PartitionService partitionService;
    private final VenueService venueService;

    @Override
    public boolean handle(TimePeriodReserveReqDTO timePeriodReserveReqDTO) {
        TimePeriodDO timePeriodDO = timePeriodReserveReqDTO.getTimePeriodDO();
        /// 校验是否到达预订时间(每个场馆统一管理提前可预订的时间)
        Long venueId = partitionService.getVenueIdById(timePeriodDO.getPartitionId());
        VenueDO venueDO = venueService.getVenueDOById(venueId);
        if (System.currentTimeMillis() <
                DateUtil.combineLocalDateAndLocalTimeToDateTimeMill(
                        timePeriodDO.getPeriodDate(),
                        venueDO.getStartBookingTime())
                        // - 1000 增加时间缓冲，提前1秒开放预订，避免时间同步问题
                        - venueDO.getAdvanceBookingDay() * 86400000 - 1000) {
            // --if-- 当前时间还没有到预定时间
            throw new ClientException(BaseErrorCode.TIME_NOT_ARRIVE_RESERVE_ERROR);
        }

        /// 校验是否已经过了场开始时间
        if (System.currentTimeMillis() >
                DateUtil.combineLocalDateAndLocalTimeToDateTimeMill(
                        timePeriodDO.getPeriodDate(),
                        timePeriodDO.getBeginTime())) {
            // --if-- 当前时间已经过了场的开始时间
            throw new ClientException(BaseErrorCode.TIME_PERIOD_MISS_ERROR);
        }

        timePeriodReserveReqDTO.setVenueId(venueId);
        timePeriodReserveReqDTO.setVenueDO(venueDO);
        return false;
    }

    @Override
    public String name() {
        return ChainConstant.RESERVE_CHAIN_NAME;
    }

    @Override
    public int order() {
        return 10;
    }
}
