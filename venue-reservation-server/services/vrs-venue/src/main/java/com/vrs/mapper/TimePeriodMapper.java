package com.vrs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrs.domain.entity.TimePeriodDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dam
 * @description 针对表【time_period_0】的数据库操作Mapper
 * @createDate 2024-11-17 16:35:42
 * @Entity generator.domain.TimePeriod
 */
public interface TimePeriodMapper extends BaseMapper<TimePeriodDO> {

    void updateStockAndBookedSlots(@Param("timePeriodId") Long timePeriodId, @Param("partitionId") Long partitionId, @Param("partitionIndex") Long partitionIndex);

    void restoreStockAndBookedSlots(@Param("timePeriodId") Long timePeriodId, @Param("partitionId") Long partitionId, @Param("partitionIndex") Long partitionIndex);

    /**
     * 批量插入的时候，如果有的数据插入失败，不影响其他数据的插入成功
     * @param timePeriodDOList
     */
    void insertBatchIgnore(@Param("timePeriodDOList") List<TimePeriodDO> timePeriodDOList);
}




