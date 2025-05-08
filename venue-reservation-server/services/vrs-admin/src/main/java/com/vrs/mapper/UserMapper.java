package com.vrs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrs.domain.entity.UserDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author dam
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-11-15 16:52:24
* @Entity generator.domain.User
*/
public interface UserMapper extends BaseMapper<UserDO> {

    void saveBatchIgnore(@Param("userDOBatch") List<UserDO> userDOBatch);

}




