package com.vrs;

import com.vrs.service.TimePeriodModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 时间段预定缓存预热
 *
 * @Author dam
 * @create 2025/1/12 15:06
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {VrsVenueApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TimePeriodGenerateTest {
    @Autowired
    private TimePeriodModelService timePeriodModelService;

    @Test
    public void generate() throws Exception{
        for (int i = 0; i < 16; i++) {
            // 执行时间段生成
            timePeriodModelService.generateTimePeriodByModelOptimize(i,false);
        }
    }

}
