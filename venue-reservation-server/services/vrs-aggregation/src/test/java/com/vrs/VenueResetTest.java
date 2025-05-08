package com.vrs;

import com.github.javafaker.Faker;
import com.vrs.domain.dto.req.VenuePicUploadReqDTO;
import com.vrs.domain.entity.OrganizationDO;
import com.vrs.domain.entity.VenueDO;
import com.vrs.enums.VenueTypeEnum;
import com.vrs.service.OrganizationService;
import com.vrs.service.VenueService;
import com.vrs.service.geo.TencentGeoApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * 时间段预定缓存预热
 *
 * @Author dam
 * @create 2025/1/12 15:06
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {VrsVenueApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VenueResetTest {

    @Autowired
    private VenueService venueService;
    @Autowired
    private OrganizationService organizationService;
    private final Faker faker = new Faker(Locale.CHINA);

    private ScheduledExecutorService executorService;
    @Autowired
    private TencentGeoApi tencentGeoApi;
    private String geoApiKey;

    @Test
    public void reset() throws Exception {

        /**
         * DELETE FROM picture_0 WHERE item_type = 0;
         * DELETE FROM picture_1 WHERE item_type = 0;
         * DELETE FROM picture_2 WHERE item_type = 0;
         * DELETE FROM picture_3 WHERE item_type = 0;
         * DELETE FROM picture_4 WHERE item_type = 0;
         * DELETE FROM picture_5 WHERE item_type = 0;
         * DELETE FROM picture_6 WHERE item_type = 0;
         * DELETE FROM picture_7 WHERE item_type = 0;
         * DELETE FROM picture_8 WHERE item_type = 0;
         * DELETE FROM picture_9 WHERE item_type = 0;
         * DELETE FROM picture_10 WHERE item_type = 0;
         * DELETE FROM picture_11 WHERE item_type = 0;
         * DELETE FROM picture_12 WHERE item_type = 0;
         * DELETE FROM picture_13 WHERE item_type = 0;
         * DELETE FROM picture_14 WHERE item_type = 0;
         * DELETE FROM picture_15 WHERE item_type = 0;
         */


        List<OrganizationDO> list = organizationService.list();
        List<Long> organizationIdList = list.stream().map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        Random random = new Random();
        VenueTypeEnum[] allTypes = VenueTypeEnum.getAllTypes();

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

        List<VenueDO> venueDOList = venueService.list();

        String[] venueNamePrefixes = {
                // 双字前缀
                "金源", "阳光", "活力", "卓越", "冠军", "动力", "巅峰", "荣耀", "星辰", "飞跃",
                "腾飞", "雄风", "凌云", "劲浪", "天行", "威龙", "豪迈", "锐力", "超凡", "领航",
                "澎湃", "雷霆", "飓风", "烈焰", "傲世", "纵横", "无限", "极速", "天韵", "龙腾",
                "星耀", "风云", "勇士", "猎鹰", "猛虎", "战狼", "雄鹰", "麒麟", "凤凰", "鲲鹏",

                // 三字前缀
                "新世纪", "未来星", "梦想家", "全明星", "超级星", "能量堡", "极限派", "自由风",
                "欢乐谷", "动感地", "青春派", "荣耀堂", "冠军营", "运动家", "健康源", "活力派",

                // 地域相关
                "京华", "沪上", "粤动", "川渝", "江浙", "京津冀", "长三角", "珠三角", "黄浦", "西湖",

                // 国际范
                "奥体", "亚运", "世博", "奥运", "环球", "国际", "亚洲", "太平洋", "大西洋"
        };

        for (VenueDO venueDO : venueDOList) {
            VenueTypeEnum typeEnum = allTypes[random.nextInt(allTypes.length)];
            venueDO.setName(venueNamePrefixes[random.nextInt(venueNamePrefixes.length)] + typeEnum.getValue() + "馆");
            venueDO.setType(typeEnum.getType());
            VenuePicUploadReqDTO venuePicUploadReqDTO = new VenuePicUploadReqDTO();
            venuePicUploadReqDTO.setVenueId(venueDO.getId());
            List<String> picList = new ArrayList<>();
            picList.add(picArr[typeEnum.getType()]);
            venuePicUploadReqDTO.setPictureList(picList);
            venueService.savePicList(venuePicUploadReqDTO);
        }
        venueService.updateBatchById(venueDOList);
    }

    @Test
    public void resetPic() throws Exception {

        /**
         * DELETE FROM picture_0 WHERE item_type = 0;
         * DELETE FROM picture_1 WHERE item_type = 0;
         * DELETE FROM picture_2 WHERE item_type = 0;
         * DELETE FROM picture_3 WHERE item_type = 0;
         * DELETE FROM picture_4 WHERE item_type = 0;
         * DELETE FROM picture_5 WHERE item_type = 0;
         * DELETE FROM picture_6 WHERE item_type = 0;
         * DELETE FROM picture_7 WHERE item_type = 0;
         * DELETE FROM picture_8 WHERE item_type = 0;
         * DELETE FROM picture_9 WHERE item_type = 0;
         * DELETE FROM picture_10 WHERE item_type = 0;
         * DELETE FROM picture_11 WHERE item_type = 0;
         * DELETE FROM picture_12 WHERE item_type = 0;
         * DELETE FROM picture_13 WHERE item_type = 0;
         * DELETE FROM picture_14 WHERE item_type = 0;
         * DELETE FROM picture_15 WHERE item_type = 0;
         */

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

        List<VenueDO> venueDOList = venueService.list();

        for (VenueDO venueDO : venueDOList) {
            VenuePicUploadReqDTO venuePicUploadReqDTO = new VenuePicUploadReqDTO();
            venuePicUploadReqDTO.setVenueId(venueDO.getId());
            List<String> picList = new ArrayList<>();
            picList.add(picArr[venueDO.getType()]);
            venuePicUploadReqDTO.setPictureList(picList);
            venueService.savePicList(venuePicUploadReqDTO);
        }
        venueService.updateBatchById(venueDOList);
    }


}
