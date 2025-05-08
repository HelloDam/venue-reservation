package com.vrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrs.convention.page.PageResponse;
import com.vrs.domain.dto.req.TimePeriodModelListReqDTO;
import com.vrs.domain.entity.TimePeriodModelDO;

/**
 * @author dam
 * @description 针对表【time_period_model_0】的数据库操作Service
 * @createDate 2024-11-17 14:29:46
 */
public interface TimePeriodModelService extends IService<TimePeriodModelDO> {

    void generateTimePeriodByModelOptimize(int tableIndex, boolean isCacheTimePeriod);

    void generateTimePeriodByModel(int tableIndex, boolean isCacheTimePeriod);

    void insert(TimePeriodModelDO timePeriodModelDO);

    PageResponse<TimePeriodModelDO> pageTimePeriodModelDO(TimePeriodModelListReqDTO request);

    void validateUserType();
}
