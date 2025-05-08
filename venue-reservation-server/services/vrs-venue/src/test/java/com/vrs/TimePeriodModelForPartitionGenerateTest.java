package com.vrs;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 可预定时间段id CSV 导出
 *
 * @Author dam
 * @create 2025/1/12 15:06
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {VrsVenueApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TimePeriodModelForPartitionGenerateTest {

    @Autowired
    private TimePeriodModelService timePeriodModelService;
    @Autowired
    private PartitionService partitionService;

    @Test
    public void generate() throws Exception {
        List<PartitionDO> partitionDOList = partitionService.list();
        for (PartitionDO partitionDO : partitionDOList) {
            List<TimePeriodModelDO> timePeriodModelDOList = new ArrayList<>();

            // 定义起始时间和结束时间
            String startTimeStr = "8:00";
            String endTimeStr = "21:00";

            // 解析起始时间和结束时间
            Date startTime = parseTime(startTimeStr);
            Date endTime = parseTime(endTimeStr);

            // 循环生成时间段
            Date currentBeginTime = startTime;
            while (currentBeginTime.before(endTime)) {
                // 计算当前时间段的结束时间（当前时间 + 1小时）
                Date currentEndTime = addHours(currentBeginTime, 1);

                // 如果当前结束时间超过总结束时间，则调整为总结束时间
                if (currentEndTime.after(endTime)) {
                    currentEndTime = endTime;
                }

                // 创建时间段对象并添加到列表
                timePeriodModelDOList.add(TimePeriodModelDO.builder()
                        .partitionId(partitionDO.getId())
                        .price(new BigDecimal(30))  // 使用你提供的 price 值
                        .beginTime(currentBeginTime)  // 当前时间段的开始时间
                        .endTime(currentEndTime)  // 当前时间段的结束时间
                        .effectiveStartDate(parseDate("2024-12-31"))  // 解析日期字符串为 Date 类型
                        .effectiveEndDate(parseDate("2026-12-30"))  // 解析日期字符串为 Date 类型
                        .lastGeneratedDate(parseDate("2024-12-31"))  // 解析日期字符串为 Date 类型
                        .status(0)  // 使用你提供的 status 值
                        .build());

                // 将当前开始时间设置为下一个时间段的开始时间
                currentBeginTime = currentEndTime;
            }

            timePeriodModelService.saveBatch(timePeriodModelDOList);
        }
    }

    // 解析时间字符串为 Date 类型（格式：HH:mm）
    private Date parseTime(String timeStr) throws ParseException {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        return timeFormat.parse(timeStr);
    }

    // 解析日期字符串为 Date 类型（格式：yyyy-MM-dd）
    private Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(dateStr);
    }

    // 为 Date 类型的时间增加指定小时数
    private Date addHours(Date date, int hours) {
        long timeInMillis = date.getTime();
        return new Date(timeInMillis + (hours * 60 * 60 * 1000));
    }
}
