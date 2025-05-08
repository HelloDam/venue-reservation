package com.vrs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrs.domain.entity.TimePeriodModelDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author dam
* @description 针对表【time_period_model_0】的数据库操作Mapper
* @createDate 2024-11-17 14:29:46
* @Entity generator.domain.TimePeriodModel
*/
public interface TimePeriodModelMapper extends BaseMapper<TimePeriodModelDO> {
    List<TimePeriodModelDO> selectOverlapModel(@Param("period") TimePeriodModelDO timePeriodModelDO);

    void updateLastGeneratedDateBatch(@Param("timePeriodDOModelUpdateBatch") List<TimePeriodModelDO> timePeriodDOModelUpdateBatch);
}




