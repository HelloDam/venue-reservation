package com.vrs.service.chainHander.reserve;

import com.vrs.chain_of_responsibility.AbstractChainHandler;
import com.vrs.common.context.UserContext;
import com.vrs.constant.ChainConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ServiceException;
import com.vrs.domain.dto.req.TimePeriodReserveReqDTO;
import com.vrs.domain.entity.VenueDO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 校验用户是否为相应机构
 *
 * @Author dam
 * @create 2024/12/11 19:43
 */
@Component
@RequiredArgsConstructor
public class OrganizationValidHandler implements AbstractChainHandler<TimePeriodReserveReqDTO> {

    @Override
    public boolean handle(TimePeriodReserveReqDTO timePeriodReserveReqDTO) {
        VenueDO venueDO = timePeriodReserveReqDTO.getVenueDO();
        Integer isOpen = venueDO.getIsOpen();
        if (isOpen == 0 && UserContext.getOrganizationId() != venueDO.getOrganizationId()) {
            // --if-- 用户不属于相应结构，且该时间段不开放给外人
            throw new ServiceException(BaseErrorCode.USER_NOT_BELONG_ORGANIZATION_ERROR);
        }
        return false;
    }

    @Override
    public String name() {
        return ChainConstant.RESERVE_CHAIN_NAME;
    }

    @Override
    public int order() {
        return 20;
    }
}
