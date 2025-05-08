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
public class TimePeriodGenerateSpeedTest {
    @Autowired
    private TimePeriodModelService timePeriodModelService;

    /**
     * 传统版本的性能测试
     *
     * @throws Exception
     */
    @Test
    public void traditionalGenerateTest() throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 16; i++) {
            // 执行时间段生成
            timePeriodModelService.generateTimePeriodByModel(i, true);
        }
        System.out.println("执行时间：" + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * 优化版本的性能测试
     *
     * @throws Exception
     */
    @Test
    public void optimizeGenerateTest() throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 16; i++) {
            // 执行时间段生成
            timePeriodModelService.generateTimePeriodByModelOptimize(i, true);
        }
        System.out.println("执行时间：" + (System.currentTimeMillis() - start) + "ms");
    }

}
