package com.vrs;

import com.vrs.domain.entity.VenueDO;
import com.vrs.enums.VenueTypeEnum;
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
public class VenueTypeSetTest {
    @Autowired
    private VenueService venueService;

    @Test
    public void generate() throws Exception {
        Random random = new Random();
        VenueTypeEnum[] allVenueTypes = VenueTypeEnum.values();
        List<VenueDO> venueDOList = venueService.list();
        for (VenueDO venueDO :venueDOList) {
            venueDO.setType(allVenueTypes[random.nextInt(allVenueTypes.length - 1)].getType());
        }
        venueService.updateBatchById(venueDOList);
    }

}
