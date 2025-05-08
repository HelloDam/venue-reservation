package com.vrs;

import com.vrs.domain.dto.req.PartitionPicUploadReqDTO;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.service.PartitionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
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
public class PartitionPicGenerateTest {
    @Autowired
    private PartitionService partitionService;

    @Test
    public void generate() throws Exception {

        /**
         * DELETE FROM picture_0 WHERE item_type = 1;
         * DELETE FROM picture_1 WHERE item_type = 1;
         * DELETE FROM picture_2 WHERE item_type = 1;
         * DELETE FROM picture_3 WHERE item_type = 1;
         * DELETE FROM picture_4 WHERE item_type = 1;
         * DELETE FROM picture_5 WHERE item_type = 1;
         * DELETE FROM picture_6 WHERE item_type = 1;
         * DELETE FROM picture_7 WHERE item_type = 1;
         * DELETE FROM picture_8 WHERE item_type = 1;
         * DELETE FROM picture_9 WHERE item_type = 1;
         * DELETE FROM picture_10 WHERE item_type = 1;
         * DELETE FROM picture_11 WHERE item_type = 1;
         * DELETE FROM picture_12 WHERE item_type = 1;
         * DELETE FROM picture_13 WHERE item_type = 1;
         * DELETE FROM picture_14 WHERE item_type = 1;
         * DELETE FROM picture_15 WHERE item_type = 1;
         */

        List<PartitionDO> list = partitionService.list();
        String[] picArr = {
                "/pic/2025/5/6/篮球馆.png",      // 0-BASKET_BALL
                "/pic/2025/5/6/足球场.png",     // 1-FOOT_BALL
                "/pic/2025/5/6/羽毛球场.png",   // 2-BADMINTON
                "/pic/2025/5/6/排球场.png",     // 3-VOLLEYBALL
                "/pic/2025/5/6/乒乓球.png",
                "/pic/2025/5/6/网球场.png",     // 5-TENNIS
                "/pic/2025/5/6/游泳池.png",     // 6-SWIMMING
                "/pic/2025/5/6/健身房.png",     // 7-FITNESS_CENTER
                "/pic/2025/5/6/手球场.png",     // 8-HANDBALL
                "/pic/2025/5/6/滑冰.png",       // 9-ICE_SKATING
                "/pic/2025/5/6/滑板馆.png",     // 10-SKATEBOARDING
                "/pic/2025/5/6/攀岩.png",       // 11-CLIMBING
                "/pic/2025/5/6/射箭.png",       // 12-ARCHERY
                "/pic/2025/5/6/拳击.png",       // 13-BOXING
                "/pic/2025/5/6/马术.png",       // 14-EQUESTRIAN
                "/pic/2025/5/6/橄榄球.png",     // 15-RUGBY
                "/pic/2025/5/6/曲棍.png",       // 16-HOCKEY
                "/pic/2025/5/6/综合.png"       // 17-SYNTHESIS
        };

        for (PartitionDO partitionDO : list) {
            PartitionPicUploadReqDTO partitionPicUploadReqDTO = new PartitionPicUploadReqDTO();
            partitionPicUploadReqDTO.setPartitionId(partitionDO.getId());
            List<String> picList = new ArrayList<>();
            if (partitionDO.getType()>17){
                continue;
            }
            picList.add(picArr[partitionDO.getType()]);
            partitionPicUploadReqDTO.setPictureList(picList);
            partitionService.savePicList(partitionPicUploadReqDTO);
        }
    }

}
