package com.vrs;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.vrs.domain.entity.TimePeriodModelDO;
import com.vrs.service.TimePeriodModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 时间段预定缓存预热
 *
 * @Author dam
 * @create 2025/1/12 15:06
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {VrsVenueApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TimePeriodModelPriceSetTest {

    @Autowired
    private TimePeriodModelService timePeriodModelService;

    @Test
    public void generate() throws Exception {
        Random random = new Random();

        // 查询所有记录
        List<TimePeriodModelDO> timePeriodModelDOList = timePeriodModelService.list();

        for (TimePeriodModelDO original : timePeriodModelDOList) {
            // 使用UpdateWrapper来更新
            LambdaUpdateWrapper<TimePeriodModelDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TimePeriodModelDO::getId, original.getId())
                    .eq(TimePeriodModelDO::getPartitionId, original.getPartitionId()) // 添加分片键作为条件
                    .set(TimePeriodModelDO::getPrice, new BigDecimal(random.nextInt(10) + 5))
                    .set(TimePeriodModelDO::getUpdateTime, LocalDateTime.now());

            timePeriodModelService.update(updateWrapper);
        }
    }
}
