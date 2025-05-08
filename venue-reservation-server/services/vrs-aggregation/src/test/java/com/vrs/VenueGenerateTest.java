package com.vrs;

import com.github.javafaker.Faker;
import com.vrs.domain.dto.req.VenuePicUploadReqDTO;
import com.vrs.domain.entity.OrganizationDO;
import com.vrs.domain.entity.VenueDO;
import com.vrs.enums.VenueTypeEnum;
import com.vrs.service.OrganizationService;
import com.vrs.service.VenueService;
import com.vrs.service.geo.TencentGeoApi;
import com.vrs.utils.ChinaGeoLocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
public class VenueGenerateTest {

    @Autowired
    private VenueService venueService;
    @Autowired
    private OrganizationService organizationService;
    private final Faker faker = new Faker(Locale.CHINA);

    private ScheduledExecutorService executorService;
    @Autowired
    private TencentGeoApi tencentGeoApi;
    private String geoApiKey;

    @Before
    public void setUp() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @After
    public void tearDown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    @Test
    public void schedule() throws Exception {
        // 每秒调用一次generate方法，持续10秒后停止
        executorService.scheduleAtFixedRate(() -> {
            try {
                System.out.println("执行定时任务");
                generate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);

        // 让测试运行一段时间，否则测试会立即结束
        Thread.sleep(24 * 60 * 1000); // 10秒后停止
    }

    public void generate() throws Exception {

        List<OrganizationDO> list = organizationService.list();
        List<Long> organizationIdList = list.stream().map(item -> {
            return item.getId();
        }).collect(Collectors.toList());
        Random random = new Random();
        List<Integer> allTypes = VenueTypeEnum.getAllTypeCode();

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

        String[] venueNameSuffixes = {
                // 标准后缀
                "体育中心", "运动馆", "健身中心", "体育馆", "运动天地", "健身会所", "体育公园", "运动广场",

                // 专业场馆
                "篮球馆", "羽毛球馆", "游泳馆", "网球中心", "足球场", "乒乓球馆", "格斗馆", "瑜伽会所",
                "跆拳道馆", "击剑俱乐部", "攀岩馆", "滑雪场", "滑冰场", "保龄球馆", "台球俱乐部",

                // 综合体
                "运动综合体", "体育MALL", "健身俱乐部", "运动生活馆", "健康城", "运动小镇", "体育产业园",

                // 特色
                "运动空间", "健身工场", "活力站", "能量站", "训练营", "运动基地", "健身工作室", "运动实验室",

                // 高端
                "运动名仕会", "精英体育会", "尊尚健身", "皇家体育", "名流运动", "贵族健身",

                // 创意
                "运动+", "动立方", "燃脂工厂", "型动派", "酷动城", "跃动空间", "劲动地带"
        };

        // 还可以添加一些特殊组合规则，例如：
        String[] specialFormats = {
                "%s国际%s",    // 如"金源国际运动中心"
                "%s%s俱乐部",  // 如"阳光健身俱乐部"
                "%s城市%s",    // 如"活力城市运动馆"
                "%s精英%s",    // 如"卓越精英训练营"
                "%s全民%s"     // 如"冠军全民健身中心"
        };

        String[] picArr = {
                "/pic/2025/5/6/橄榄球-9b879324ae634a16a7ca1d68ddef0dd6.png",
                "/pic/2025/5/6/滑板馆-0d15f8929c364fdb87bbc8b3a9bee64b.png",
                "/pic/2025/5/6/滑冰-cc91b3bb1a364f81b8f33f4c4fefb564.png",
                "/pic/2025/5/6/健身房-288ac77624934bcd90e4e50c4cf406c2.png",
                "/pic/2025/5/6/篮球馆-7f2cd9b992c64cf2b3619f3093611e0f.pn",
                "/pic/2025/5/6/马术-e46147f041254727aefe569f247aa043.png",
                "/pic/2025/5/6/排球场-d2ec981afcd0448bae1400000a6e3949.png",
                "/pic/2025/5/6/攀岩-130326305ba84b3899455986405327b7.png",
                "/pic/2025/5/6/曲棍-114feccf28d24229941d2209524c78da.png",
                "/pic/2025/5/6/拳击-61d450b343eb4ca198ced9d0328a8b96.png",
                "/pic/2025/5/6/射箭-b0e5c082621d4de0b99b003a299a8c6b.png",
                "/pic/2025/5/6/手球场-8a29e0397ff04647b52ca73aeb664bbd.png",
                "/pic/2025/5/6/网球场-6ff26862a4d84d738f546f1970c73110.png",
                "/pic/2025/5/6/游泳池-c59c3a8b63184ea49cbb865121218680.png",
                "/pic/2025/5/6/羽毛球场-7d13213d4e5145069ef083d43dc6c580.png",
                "/pic/2025/5/6/综合-b6ea89dcbbcd424d98db977ccda2cd4b.png",
                "/pic/2025/5/6/足球场-60a31135054d4c11b9154e9d907d5098.png"
        };


        for (int i = 0; i < 1; i++) {
            List<VenueDO> venueDOList = new ArrayList<>();

            for (int j = 0; j < 3; j++) {
                VenueDO venueDO = new VenueDO();
                venueDO.setOrganizationId(organizationIdList.get(random.nextInt(organizationIdList.size())));

                // 随机选择生成方式
                int nameStyle = random.nextInt(10); // 0-9

                String venueName;
                if (nameStyle < 7) { // 70%概率使用标准前缀+后缀
                    venueName = venueNamePrefixes[random.nextInt(venueNamePrefixes.length)] +
                            venueNameSuffixes[random.nextInt(venueNameSuffixes.length)];
                } else if (nameStyle < 9) { // 20%概率使用特殊格式
                    String format = specialFormats[random.nextInt(specialFormats.length)];
                    venueName = String.format(format,
                            venueNamePrefixes[random.nextInt(venueNamePrefixes.length)],
                            venueNameSuffixes[random.nextInt(venueNameSuffixes.length)]);
                } else { // 10%概率使用三部分组合
                    venueName = venueNamePrefixes[random.nextInt(venueNamePrefixes.length)] +
                            venueNamePrefixes[random.nextInt(venueNamePrefixes.length)] +
                            venueNameSuffixes[random.nextInt(venueNameSuffixes.length)];
                }
                venueDO.setName(venueName);

                venueDO.setType(allTypes.get(random.nextInt(allTypes.size())));
//                BigDecimal[] location = ChinaGeoLocation.randomLocationInChina();
//                BigDecimal[] location = ChinaGeoLocation.randomNearbyLocation(34.131, 108.8, 1000);
//                BigDecimal[] location = ChinaGeoLocation.randomNearbyLocation(23.038254, 113.393178, 500);
                BigDecimal[] location = ChinaGeoLocation.randomNearbyLocation(22.3193039, 114.1693611, 2);
                venueDO.setAddress(tencentGeoApi.getAddressByLocation(location[1], location[0]));
                venueDO.setDescription("提供多种运动器材，拥有专业的教练团队");
                venueDO.setOpenTime("周一至周五: 09:00-21:00, 周六日: 08:00-22:00");
                String phoneNumber = "1" + faker.number().numberBetween(30, 99) +
                        faker.number().digits(8);
                venueDO.setPhoneNumber(phoneNumber);
                venueDO.setStatus(1);
                venueDO.setIsOpen(1);
                venueDO.setAdvanceBookingDay(3);
                venueDO.setStartBookingTime(LocalTime.of(7, 0));
                venueDO.setLatitude(location[0]);
                venueDO.setLongitude(location[1]);
                venueDOList.add(venueDO);
            }
            venueService.saveBatch(venueDOList);
            for (VenueDO venueDO : venueDOList) {
                VenuePicUploadReqDTO venuePicUploadReqDTO = new VenuePicUploadReqDTO();
                venuePicUploadReqDTO.setVenueId(venueDO.getId());
                List<String> picList = new ArrayList<>();
                picList.add(picArr[random.nextInt(picArr.length)]);
                venuePicUploadReqDTO.setPictureList(picList);
                venueService.savePicList(venuePicUploadReqDTO);
            }
        }
    }

}
