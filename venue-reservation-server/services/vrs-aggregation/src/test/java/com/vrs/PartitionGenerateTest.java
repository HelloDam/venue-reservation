package com.vrs;

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

import java.util.ArrayList;
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
public class PartitionGenerateTest {
    @Autowired
    private VenueService venueService;
    @Autowired
    private PartitionService partitionService;

    @Test
    public void generate() throws Exception {
        Random random = new Random();
        List<VenueDO> venueDOList = venueService.list();
        for (VenueDO venueDO : venueDOList) {
            List<PartitionDO> partitionDOList = new ArrayList<>();
            int num = random.nextInt(5) + 1; // Ensure at least 1 partition

            // Special handling for synthetic venue type
            boolean isSynthetic = venueDO.getType() == 100000;

            // Get all possible partition types except synthetic (since it's a venue type only)
            PartitionTypeEnum[] allPartitionTypes = PartitionTypeEnum.values();

            // For single-type venues, get the sport name once
            String sportName = isSynthetic ? null : VenueTypeEnum.findValueByType(venueDO.getType());

            for (int i = 0; i < num; i++) {
                PartitionDO partitionDO = new PartitionDO();
                partitionDO.setVenueId(venueDO.getId());

                // Set type and name based on venue type
                if (isSynthetic) {
                    // For synthetic venues, choose a random partition type
                    PartitionTypeEnum randomType = allPartitionTypes[random.nextInt(allPartitionTypes.length)];
                    partitionDO.setType(randomType.getType());
                    partitionDO.setName(randomType.getValue() + "区");
                } else {
                    // For single-type venues, use the same type as the venue
                    partitionDO.setType(venueDO.getType());
                    // Name format: "乒乓球A区", "乒乓球B区", etc.
                    char partitionChar = (char) ('A' + i);
                    partitionDO.setName(sportName + partitionChar + "区");
                }

                // Set description based on the name
                partitionDO.setDescription(partitionDO.getName() + "的描述信息");
                partitionDO.setNum(10 + random.nextInt(30));
                partitionDO.setStatus(1);
                partitionDOList.add(partitionDO);
            }
            partitionService.saveBatch(partitionDOList);
        }
    }

}
