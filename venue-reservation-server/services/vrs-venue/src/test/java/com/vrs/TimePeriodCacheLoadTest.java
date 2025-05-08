package com.vrs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.service.PartitionService;
import com.vrs.service.TimePeriodService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

/**
 * 时间段预定缓存预热
 *
 * @Author dam
 * @create 2025/1/12 15:06
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {VrsVenueApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TimePeriodCacheLoadTest {

    @Autowired
    private TimePeriodService timePeriodService;
    @Autowired
    private PartitionService partitionService;

    @Test
    public void generate() throws Exception {
        QueryWrapper<TimePeriodDO> queryWrapper = new QueryWrapper<>();
        // 只查询在今天和今天之后的可预订时间段
        queryWrapper.ge("period_date", LocalDate.now());
        List<TimePeriodDO> timePeriodDOList = timePeriodService.list(queryWrapper);
        for (TimePeriodDO timePeriodDO : timePeriodDOList) {
            if (timePeriodDO.getPeriodDate().isAfter(LocalDate.now())) {
                loadCache(timePeriodDO);
            } else if (timePeriodDO.getPeriodDate().isEqual(LocalDate.now())) {
                loadCache(timePeriodDO);
            }
        }
    }

    private void loadCache(TimePeriodDO timePeriodDO) {
        timePeriodService.getTimePeriodDOById(timePeriodDO.getId());
        PartitionDO partitionDO = partitionService.getPartitionDOById(timePeriodDO.getPartitionId());
        // 首先检测空闲场号缓存有没有加载好，没有的话进行加载
        timePeriodService.checkBitMapCache(String.format(RedisCacheConstant.VENUE_TIME_PERIOD_FREE_INDEX_BIT_MAP_KEY, timePeriodDO.getId()), timePeriodDO.getId(), partitionDO.getNum());
        // 其次检测时间段库存有没有加载好，没有的话进行加载
        timePeriodService.getStockByTimePeriodId(timePeriodDO.getId());
    }
}
