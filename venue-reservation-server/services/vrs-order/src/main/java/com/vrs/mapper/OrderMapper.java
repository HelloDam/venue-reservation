package com.vrs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrs.domain.entity.OrderDO;
import org.apache.ibatis.annotations.Param;

/**
* @author dam
* @description 针对表【order】的数据库操作Mapper
* @createDate 2024-11-30 19:03:04
* @Entity generator.domain.Order
*/
public interface OrderMapper extends BaseMapper<OrderDO> {

    OrderDO selectByOrderSn(@Param("orderSn") String orderSn);

    void updateByOrderSn(@Param("orderDO") OrderDO orderDO);

    Integer selectStatusByOrderSn(@Param("orderSn") String orderSn);

    void updateStatusByOrderSn(@Param("orderSn") String orderSn, @Param("orderStatus") int orderStatus);
}




