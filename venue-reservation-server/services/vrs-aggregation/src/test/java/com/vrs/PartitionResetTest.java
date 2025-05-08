package com.vrs;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.VenueDO;
import com.vrs.enums.PartitionTypeEnum;
import com.vrs.enums.VenueTypeEnum;
import com.vrs.service.PartitionService;
import com.vrs.service.VenueService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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
public class PartitionResetTest {
    @Autowired
    private VenueService venueService;
    @Autowired
    private PartitionService partitionService;

    @Test
    public void generate() throws Exception {

        PartitionTypeEnum[] allPartitionTypes = PartitionTypeEnum.values();
        Random random = new Random();
        List<VenueDO> venueDOList = venueService.list();
        for (VenueDO venueDO : venueDOList) {
            LambdaQueryWrapper<PartitionDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PartitionDO::getVenueId, venueDO.getId());
            List<PartitionDO> partitionDOList = partitionService.list(queryWrapper);
            for (int i = 0; i < partitionDOList.size(); i++) {
                PartitionDO partitionDO = partitionDOList.get(i);
                boolean isSynthetic = venueDO.getType() == VenueTypeEnum.SYNTHESIS.getType();
                if (isSynthetic) {
                    PartitionTypeEnum randomType = allPartitionTypes[random.nextInt(allPartitionTypes.length)];
                    partitionDO.setType(randomType.getType());
                    partitionDO.setName(randomType.getValue() + "区");
                } else {
                    partitionDO.setType(venueDO.getType());
                    // Name format: "乒乓球A区", "乒乓球B区", etc.
                    char partitionChar = (char) ('A' + i);
                    partitionDO.setName(venueDO.getName() + partitionChar + "区");
                }
                partitionDO.setVenueId(null);
            }
            partitionService.updateBatchById(partitionDOList);
        }

    }

}
