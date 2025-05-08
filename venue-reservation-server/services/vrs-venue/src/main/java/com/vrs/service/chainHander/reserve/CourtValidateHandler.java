package com.vrs.service.chainHander.reserve;

import com.vrs.chain_of_responsibility.AbstractChainHandler;
import com.vrs.constant.ChainConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ServiceException;
import com.vrs.domain.dto.req.TimePeriodReserveReqDTO;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.service.PartitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 校验所预定时间段是否为空
 *
 * @Author dam
 * @create 2024/12/11 19:43
 */
@Component
@RequiredArgsConstructor
public class CourtValidateHandler implements AbstractChainHandler<TimePeriodReserveReqDTO> {

    private final PartitionService partitionService;

    @Override
    public boolean handle(TimePeriodReserveReqDTO timePeriodReserveReqDTO) {
        PartitionDO partitionDO = partitionService.getPartitionDOById(timePeriodReserveReqDTO.getTimePeriodDO().getPartitionId());
        if (timePeriodReserveReqDTO.getCourtIndex() < -1 || timePeriodReserveReqDTO.getCourtIndex() >= partitionDO.getNum()) {
            throw new ServiceException(BaseErrorCode.COURT_ERROR_ERROR);
        }
        timePeriodReserveReqDTO.setPartitionDO(partitionDO);
        return false;
    }

    @Override
    public String name() {
        return ChainConstant.RESERVE_CHAIN_NAME;
    }

    @Override
    public int order() {
        return 5;
    }
}
