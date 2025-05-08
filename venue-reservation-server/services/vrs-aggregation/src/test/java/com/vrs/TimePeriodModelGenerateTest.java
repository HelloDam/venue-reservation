package com.vrs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.TimePeriodModelDO;
import com.vrs.service.PartitionService;
import com.vrs.service.TimePeriodModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间段预定缓存预热
 *
 * @Author dam
 * @create 2025/1/12 15:06
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {VrsVenueApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TimePeriodModelGenerateTest {
    @Autowired
    private TimePeriodModelService timePeriodModelService;
    @Autowired
    private PartitionService partitionService;

    /**
     * 所有分区的时间段模版都一样
     *
     * @throws Exception
     */
    @Test
    public void fixGenerate() throws Exception {
        Random random = new Random();
        List<PartitionDO> partitionDOList = partitionService.list();

        // Prepare date formats
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Set effective dates
        Date now = new Date();

        Calendar calendar1 = Calendar.getInstance();

        // Set effective start date to yesterday
        calendar1.setTime(now);
        calendar1.add(Calendar.DATE, -1);  // Subtract one day
        Date effectiveStartDate = calendar1.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2050, Calendar.JANUARY, 1);
        Date endDate = calendar.getTime();

        for (PartitionDO partitionDO : partitionDOList) {
            List<TimePeriodModelDO> timePeriodModelList = new ArrayList<>();

            // Generate time slots from 8:00 to 22:00, each lasting 1 hour
            for (int hour = 8; hour < 22; hour++) {
                TimePeriodModelDO timePeriodModelDO = new TimePeriodModelDO();

                // Set random price between 50-200
                timePeriodModelDO.setPrice(new BigDecimal(random.nextInt(10) + 5));

                timePeriodModelDO.setVenueId(partitionDO.getVenueId());
                timePeriodModelDO.setPartitionId(partitionDO.getId());

                // Set time period (HH:00 to (HH+1):00)
                timePeriodModelDO.setBeginTime(timeFormat.parse(String.format("%02d:00", hour)));
                timePeriodModelDO.setEndTime(timeFormat.parse(String.format("%02d:00", hour + 1)));

                // Set effective dates
                timePeriodModelDO.setEffectiveStartDate(effectiveStartDate);
                timePeriodModelDO.setEffectiveEndDate(endDate);

                // Set last generated date as now
                timePeriodModelDO.setLastGeneratedDate(effectiveStartDate);

                // Set status (0 means enabled)
                timePeriodModelDO.setStatus(0);

                timePeriodModelList.add(timePeriodModelDO);
            }

            timePeriodModelService.saveBatch(timePeriodModelList);
        }
    }

    /**
     * 使用几个不同的时间段模板方案来生成时间段模板
     *
     * @throws Exception
     */
    @Test
    public void randomGenerate() throws Exception {
        Random random = new Random();
        List<PartitionDO> partitionDOList = partitionService.list();

        // 准备日期格式
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        // 设置生效日期
        Date now = new Date();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(now);
        calendar1.add(Calendar.DATE, -1);  // 生效开始日期设为昨天
        Date effectiveStartDate = calendar1.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2050, Calendar.JANUARY, 1);
        Date endDate = calendar.getTime();

        // 定义多个时间段组合（每个组合的时间段数量不同）
        String[][][] timeSlotGroups = {
                // 组合1：4个时间段（适合小型活动）
                {
                        {"08:30", "10:00"},
                        {"10:30", "12:00"},
                        {"14:00", "15:30"},
                        {"16:00", "17:30"}
                },
                // 组合2：6个时间段（全天活动）
                {
                        {"08:00", "09:30"},
                        {"10:00", "11:30"},
                        {"13:00", "14:00"},
                        {"14:30", "15:30"},
                        {"16:00", "17:00"},
                        {"18:00", "19:30"}
                },
                // 组合3：3个时间段（半日活动）
                {
                        {"09:00", "11:00"},
                        {"13:30", "15:30"},
                        {"16:00", "18:00"}
                },
                // 组合4：7个时间段（高密度安排）
                {
                        {"08:00", "09:15"},
                        {"09:30", "10:45"},
                        {"11:00", "12:15"},
                        {"13:30", "14:45"},
                        {"15:00", "16:15"},
                        {"16:30", "17:45"},
                        {"18:30", "20:00"}
                },
                // 组合5：5个时间段（标准安排）
                {
                        {"08:00", "09:30"},
                        {"10:00", "11:30"},
                        {"13:00", "14:30"},
                        {"15:00", "16:30"},
                        {"17:00", "18:30"}
                }
        };

        for (PartitionDO partitionDO : partitionDOList) {
            List<TimePeriodModelDO> timePeriodModelList = new ArrayList<>();

            // 随机选择一个时间段组合
            String[][] selectedSlots = timeSlotGroups[random.nextInt(timeSlotGroups.length)];

            // 为每个分区生成时间段
            for (String[] slot : selectedSlots) {
                TimePeriodModelDO timePeriodModelDO = new TimePeriodModelDO();

                // 设置随机价格（5-15）
                timePeriodModelDO.setPrice(new BigDecimal(random.nextInt(10) + 5));

                timePeriodModelDO.setVenueId(partitionDO.getVenueId());
                timePeriodModelDO.setPartitionId(partitionDO.getId());

                // 设置时间段
                timePeriodModelDO.setBeginTime(timeFormat.parse(slot[0]));
                timePeriodModelDO.setEndTime(timeFormat.parse(slot[1]));

                // 设置生效日期
                timePeriodModelDO.setEffectiveStartDate(effectiveStartDate);
                timePeriodModelDO.setEffectiveEndDate(endDate);

                // 设置最后生成日期
                timePeriodModelDO.setLastGeneratedDate(effectiveStartDate);

                // 设置状态（0表示启用）
                timePeriodModelDO.setStatus(0);

                timePeriodModelList.add(timePeriodModelDO);
            }

            // 批量保存时间段
            timePeriodModelService.saveBatch(timePeriodModelList);
        }
    }

    @Test
    public void countTotal() {
        long count = timePeriodModelService.count();
        System.out.println("时间段模版总数：" + count);
    }

    @Test
    public void generateModelByPartitionId() throws Exception {
        Random random = new Random();
        Long partitionId = 1913463126689894400l;
        PartitionDO partitionDO = partitionService.getById(partitionId);

        // Prepare date formats
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Set effective dates
        Date now = new Date();

        Calendar calendar1 = Calendar.getInstance();

        // Set effective start date to yesterday
        calendar1.setTime(now);
        calendar1.add(Calendar.DATE, -1);  // Subtract one day
        Date effectiveStartDate = calendar1.getTime();

        Calendar calendar = Calendar.getInstance();
        calendar.set(2050, Calendar.JANUARY, 1);
        Date endDate = calendar.getTime();

        QueryWrapper<TimePeriodModelDO> timePeriodModelDOQueryWrapper = new QueryWrapper<>();
        timePeriodModelDOQueryWrapper.eq("partition_id", partitionId);
        timePeriodModelService.remove(timePeriodModelDOQueryWrapper);

        // Define the specific time periods
        String[][] timeSlots = {
                {"08:15", "10:00"},
                {"10:00", "11:40"},
                {"13:00", "14:00"},
                {"14:30", "16:00"},
                {"16:00", "17:00"},
                {"18:00", "19:30"},
                {"20:00", "22:00"}
        };

        // Loop through each time slot and create a TimePeriodModelDO for each
        List<TimePeriodModelDO> timePeriodModelList = new ArrayList<>();
        for (String[] slot : timeSlots) {
            TimePeriodModelDO slotDO = new TimePeriodModelDO();

            // Copy common properties
            slotDO.setPrice(new BigDecimal(random.nextInt(10) + 5));
            slotDO.setVenueId(partitionDO.getVenueId());
            slotDO.setPartitionId(partitionDO.getId());

            // Set the specific time period
            slotDO.setBeginTime(timeFormat.parse(slot[0]));
            slotDO.setEndTime(timeFormat.parse(slot[1]));

            // Set effective dates
            slotDO.setEffectiveStartDate(effectiveStartDate);
            slotDO.setEffectiveEndDate(endDate);

            // Set last generated date as now
            slotDO.setLastGeneratedDate(effectiveStartDate);

            // Set status (0 means enabled)
            slotDO.setStatus(0);

            timePeriodModelList.add(slotDO);
        }

        // Save all time periods in batch
        timePeriodModelService.saveBatch(timePeriodModelList);
    }

}
