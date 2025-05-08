package com.vrs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrs.domain.entity.PartitionDO;
import org.apache.ibatis.annotations.Param;

/**
* @author dam
* @description 针对表【partition_0】的数据库操作Mapper
* @createDate 2024-12-07 12:01:56
* @Entity generator.domain.Partition
*/
public interface PartitionMapper extends BaseMapper<PartitionDO> {

    Long getVenueIdById(@Param("partitionId") Long partitionId);
}




