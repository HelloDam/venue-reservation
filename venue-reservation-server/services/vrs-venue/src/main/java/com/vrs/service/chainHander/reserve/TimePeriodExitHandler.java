package com.vrs.service.chainHander.reserve;

import com.vrs.chain_of_responsibility.AbstractChainHandler;
import com.vrs.constant.ChainConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ServiceException;
import com.vrs.domain.dto.req.TimePeriodReserveReqDTO;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.service.TimePeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 校验所预定时间段是否为空
 * @Author dam
 * @create 2024/12/11 19:43
 */
@Component
@RequiredArgsConstructor
public class TimePeriodExitHandler implements AbstractChainHandler<TimePeriodReserveReqDTO> {

    private final TimePeriodService timePeriodService;

    @Override
    public boolean handle(TimePeriodReserveReqDTO timePeriodReserveReqDTO) {
        TimePeriodDO timePeriodDO = timePeriodService.getTimePeriodDOById(timePeriodReserveReqDTO.getTimePeriodId());
        if (timePeriodDO == null) {
            // --if-- 用户想要预订的时间段为空
            throw new ServiceException(BaseErrorCode.TIME_PERIOD_NULL_ERROR);
        }
        timePeriodReserveReqDTO.setTimePeriodDO(timePeriodDO);
        return false;
    }

    @Override
    public String name() {
        return ChainConstant.RESERVE_CHAIN_NAME;
    }

    @Override
    public int order() {
        return 0;
    }
}
