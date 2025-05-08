package com.vrs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.domain.entity.TimePeriodModelDO;
import com.vrs.service.PartitionService;
import com.vrs.service.TimePeriodModelService;
import com.vrs.service.TimePeriodService;
import com.vrs.utils.DateUtil;
import com.vrs.utils.SnowflakeIdUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class TimePeriodGenerateByPartitionIdTest {
    @Autowired
    private TimePeriodModelService timePeriodModelService;
    @Autowired
    private TimePeriodService timePeriodService;
    @Autowired
    private PartitionService partitionService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void generate() throws Exception {
        Long selectPartitionId = 1913463126689894400l;

        QueryWrapper<TimePeriodDO> periodDOQueryWrapper = new QueryWrapper<>();
        periodDOQueryWrapper.eq("partition_id", selectPartitionId);
        timePeriodService.remove(periodDOQueryWrapper);

        QueryWrapper<TimePeriodModelDO> timePeriodModelDOQueryWrapper = new QueryWrapper<>();
        timePeriodModelDOQueryWrapper.eq("partition_id", selectPartitionId);
        List<TimePeriodModelDO> list = timePeriodModelService.list(timePeriodModelDOQueryWrapper);

        PartitionDO partitionDO = partitionService.getById(selectPartitionId);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        List<TimePeriodDO> timePeriodDOList = new ArrayList<>();
        for (TimePeriodModelDO timePeriodModelDO : list) {
            BigDecimal price = timePeriodModelDO.getPrice();
            Long venueId = timePeriodModelDO.getVenueId();
            Long partitionId = timePeriodModelDO.getPartitionId();
            Date beginTime = timePeriodModelDO.getBeginTime();
            Date endTime = timePeriodModelDO.getEndTime();
            Date effectiveStartDate = timePeriodModelDO.getEffectiveStartDate();
            Date effectiveEndDate = timePeriodModelDO.getEffectiveEndDate();
            Date lastGeneratedDate = timePeriodModelDO.getLastGeneratedDate();
            Long id = timePeriodModelDO.getId();
            // 如果当前分区存在可预订时间的缓存，这里进行删除，因为生成了新的，需要重新查询数据库
            stringRedisTemplate.delete(String.format(
                    RedisCacheConstant.VENUE_TIME_PERIOD_BY_PARTITION_ID_KEY,
                    partitionId));

            // 这里其实不需要每天定时任务，都把advanceBookingDay都生成一遍，例如今天已经生成了未来七天的时间段了，那么明天其实只需要生成第八天的时间段即可，所以使用到lastGeneratedDate
            Date generateDate = null;
            for (int i = 0; i <= 7; i++) {
                // 获取要生成的日期
                generateDate = new Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000);
                if (lastGeneratedDate != null && generateDate.before(lastGeneratedDate)) {
                    // 如果对应日期的时间段已经被生成过了，直接跳过
                    continue;
                }
                // 检查明天的日期是否在这个范围内
                boolean isInDateRange = generateDate.after(effectiveStartDate) && generateDate.before(effectiveEndDate);
                if (isInDateRange) {
                    TimePeriodDO timePeriodDO = TimePeriodDO.builder()
                            .partitionId(partitionId)
                            .price(price)
                            .stock(partitionDO.getNum())
                            .bookedSlots(0L)
                            .periodDate(DateUtil.dateToLocalDate(generateDate))
                            .beginTime(DateUtil.dateToLocalTime(beginTime))
                            .endTime(DateUtil.dateToLocalTime(endTime))
                            .build();
                    timePeriodDO.setId(SnowflakeIdUtil.nextId());
                    timePeriodDOList.add(timePeriodDO);

                }
            }
            if (generateDate != null) {
                // 批量更新时间段模版的最新生成日期
                TimePeriodModelDO newTimePeriodModelDO = new TimePeriodModelDO();
                newTimePeriodModelDO.setLastGeneratedDate(generateDate);

                QueryWrapper<TimePeriodModelDO> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", id)
                        .eq("partition_id", partitionId);

                timePeriodModelService.update(newTimePeriodModelDO, queryWrapper);
            }
        }
        boolean b = timePeriodService.saveBatch(timePeriodDOList);
        System.out.println(b);

    }


}
