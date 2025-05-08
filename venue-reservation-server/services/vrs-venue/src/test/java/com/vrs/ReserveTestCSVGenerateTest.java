package com.vrs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.service.TimePeriodService;
import com.vrs.utils.TxtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
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
public class ReserveTestCSVGenerateTest {

    @Autowired
    private TimePeriodService timePeriodService;

    /**
     * csv地址
     */
    private final String csvPath = Paths.get("").toAbsolutePath().getParent().getParent() + File.separator + "tmp" + File.separator + "场馆预定时间段.csv";

    @Test
    public void generate() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("timePeriodId\n");
        QueryWrapper<TimePeriodDO> queryWrapper = new QueryWrapper<>();
        // 只查询在今天和今天之后的可预订时间段
        queryWrapper.ge("period_date", LocalDate.now());
        List<TimePeriodDO> timePeriodDOList = timePeriodService.list(queryWrapper);
        for (TimePeriodDO timePeriodDO : timePeriodDOList) {
            if (timePeriodDO.getPeriodDate().isAfter(LocalDate.now())) {
                stringBuilder.append(timePeriodDO.getId() + "\n");
            } else if (timePeriodDO.getPeriodDate().isEqual(LocalDate.now())) {
                if (timePeriodDO.getBeginTime().isAfter(LocalTime.now())){
                    stringBuilder.append(timePeriodDO.getId() + "\n");
                }
            }

        }
        TxtUtil.write(new File(csvPath), stringBuilder.toString(), "utf-8");
    }
}
