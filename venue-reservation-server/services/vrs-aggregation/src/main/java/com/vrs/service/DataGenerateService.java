package com.vrs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.javafaker.Faker;
import com.vrs.domain.dto.req.PartitionPicUploadReqDTO;
import com.vrs.domain.dto.req.VenueNearGenerateDTO;
import com.vrs.domain.dto.req.VenuePicUploadReqDTO;
import com.vrs.domain.entity.*;
import com.vrs.enums.PartitionTypeEnum;
import com.vrs.enums.VenueTypeEnum;
import com.vrs.service.geo.TencentGeoApi;
import com.vrs.utils.ChinaGeoLocation;
import com.vrs.utils.DateUtil;
import com.vrs.utils.SnowflakeIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author dam
 * @description 针对表【venue】的数据库操作Service实现
 * @createDate 2024-12-07 10:44:56
 */
@Service
@RequiredArgsConstructor
public class DataGenerateService {

    private final OrganizationService organizationService;
    private final TencentGeoApi tencentGeoApi;
    private final VenueService venueService;
    private final PartitionService partitionService;
    private final TimePeriodModelService timePeriodModelService;
    private final TimePeriodService timePeriodService;

    private final Faker faker = new Faker(Locale.CHINA);

    public void generateNearVenue(VenueNearGenerateDTO request) throws ParseException {

        CompletableFuture.runAsync(() -> {
            List<OrganizationDO> list = organizationService.list();
            List<Long> organizationIdList = list.stream().map(item -> {
                return item.getId();
            }).collect(Collectors.toList());

            VenueTypeEnum[] allTypes = VenueTypeEnum.getAllTypes();

            Random random = new Random();

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

            /// 生成场馆
            List<VenueDO> venueDOList = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 5; j++) {
                    VenueDO venueDO = new VenueDO();
                    venueDO.setId(SnowflakeIdUtil.nextId());
                    venueDO.setOrganizationId(organizationIdList.get(random.nextInt(organizationIdList.size())));
                    VenueTypeEnum typeEnum = allTypes[random.nextInt(allTypes.length)];
                    venueDO.setName(venueNamePrefixes[random.nextInt(venueNamePrefixes.length)] + typeEnum.getValue() + "馆");
                    venueDO.setType(typeEnum.getType());
                    BigDecimal[] location = ChinaGeoLocation.randomNearbyLocation(request.getLatitude(), request.getLongitude(), 5);
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
                    VenuePicUploadReqDTO venuePicUploadReqDTO = new VenuePicUploadReqDTO();
                    venuePicUploadReqDTO.setVenueId(venueDO.getId());
                    List<String> picList = new ArrayList<>();
                    picList.add(picArr[typeEnum.getType()]);
                    venuePicUploadReqDTO.setPictureList(picList);
                    venueService.savePicList(venuePicUploadReqDTO);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            venueService.saveBatch(venueDOList);
            System.out.println("生成的场馆数：" + venueDOList.size());
            venueService.cacheLocations(venueDOList);

            /// 生成分区
            // Get all possible partition types except synthetic (since it's a venue type only)
            PartitionTypeEnum[] allPartitionTypes = PartitionTypeEnum.values();
            List<PartitionDO> partitionDOList = new ArrayList<>();
            for (VenueDO venueDO : venueDOList) {
                int num = random.nextInt(5) + 1; // Ensure at least 1 partition

                // Special handling for synthetic venue type
                boolean isSynthetic = venueDO.getType() == VenueTypeEnum.SYNTHESIS.getType();

                // For single-type venues, get the sport name once
                String sportName = isSynthetic ? null : VenueTypeEnum.findValueByType(venueDO.getType());

                for (int i = 0; i < num; i++) {
                    PartitionDO partitionDO = new PartitionDO();
                    partitionDO.setId(SnowflakeIdUtil.nextId());
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

                    PartitionPicUploadReqDTO partitionPicUploadReqDTO = new PartitionPicUploadReqDTO();
                    partitionPicUploadReqDTO.setPartitionId(partitionDO.getId());
                    List<String> picList = new ArrayList<>();
                    if (partitionDO.getType() > 17) {
                        continue;
                    }
                    picList.add(picArr[partitionDO.getType()]);
                    partitionPicUploadReqDTO.setPictureList(picList);
                    partitionService.savePicList(partitionPicUploadReqDTO);
                }
            }
            System.out.println("生成的分区数：" + partitionDOList.size());
            partitionService.saveBatch(partitionDOList);

            /// 生成时间段
            // 准备日期格式
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            // 设置生效日期
            Date now = new Date();
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(now);
            calendar1.add(Calendar.DATE, -1);  // 生效开始日期设为昨天
            Date effectiveStartDate = calendar1.getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.set(2050, Calendar.JANUARY, 1);
            Date endDate = calendar.getTime();

            // 定义多个时间段组合（每个组合的时间段数量不同）
            String[][][] timeSlotGroups = {
                    // 组合1：4个时间段（适合小型活动）
                    {
                            {"08:30", "10:00"},
                            {"10:30", "12:00"},
                            {"14:00", "15:30"},
                            {"16:00", "17:30"}
                    },
                    // 组合2：6个时间段（全天活动）
                    {
                            {"08:00", "09:30"},
                            {"10:00", "11:30"},
                            {"13:00", "14:00"},
                            {"14:30", "15:30"},
                            {"16:00", "17:00"},
                            {"18:00", "19:30"}
                    },
                    // 组合3：3个时间段（半日活动）
                    {
                            {"09:00", "11:00"},
                            {"13:30", "15:30"},
                            {"16:00", "18:00"}
                    },
                    // 组合4：7个时间段（高密度安排）
                    {
                            {"08:00", "09:15"},
                            {"09:30", "10:45"},
                            {"11:00", "12:15"},
                            {"13:30", "14:45"},
                            {"15:00", "16:15"},
                            {"16:30", "17:45"},
                            {"18:30", "20:00"}
                    },
                    // 组合5：5个时间段（标准安排）
                    {
                            {"08:00", "09:30"},
                            {"10:00", "11:30"},
                            {"13:00", "14:30"},
                            {"15:00", "16:30"},
                            {"17:00", "18:30"}
                    }
            };

            List<TimePeriodModelDO> timePeriodModelList = new ArrayList<>();
            for (PartitionDO partitionDO : partitionDOList) {
                // 随机选择一个时间段组合
                String[][] selectedSlots = timeSlotGroups[random.nextInt(timeSlotGroups.length)];

                // 为每个分区生成时间段
                for (String[] slot : selectedSlots) {
                    TimePeriodModelDO timePeriodModelDO = new TimePeriodModelDO();

                    // 设置随机价格（5-15）
                    timePeriodModelDO.setPrice(new BigDecimal(random.nextInt(10) + 5));

                    timePeriodModelDO.setVenueId(partitionDO.getVenueId());
                    timePeriodModelDO.setPartitionId(partitionDO.getId());

                    // 设置时间段
                    try {
                        timePeriodModelDO.setBeginTime(timeFormat.parse(slot[0]));
                        timePeriodModelDO.setEndTime(timeFormat.parse(slot[1]));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    // 设置生效日期
                    timePeriodModelDO.setEffectiveStartDate(effectiveStartDate);
                    timePeriodModelDO.setEffectiveEndDate(endDate);

                    // 设置最后生成日期
                    timePeriodModelDO.setLastGeneratedDate(effectiveStartDate);

                    // 设置状态（0表示启用）
                    timePeriodModelDO.setStatus(0);

                    timePeriodModelList.add(timePeriodModelDO);
                }
            }

            // 批量保存时间段
            System.out.println("生成的时间段模版数：" + timePeriodModelList.size());
            timePeriodModelService.saveBatch(timePeriodModelList);

            /// 生成时间段
            HashMap<Long, PartitionDO> idAndPartitionMap = new HashMap<>();
            for (PartitionDO partitionDO : partitionDOList) {
                idAndPartitionMap.put(partitionDO.getId(), partitionDO);
            }
            List<TimePeriodDO> timePeriodDOList = new ArrayList<>();
            for (TimePeriodModelDO timePeriodModelDO : timePeriodModelList) {
                BigDecimal price = timePeriodModelDO.getPrice();
                Long venueId = timePeriodModelDO.getVenueId();
                Long partitionId = timePeriodModelDO.getPartitionId();
                Date beginTime = timePeriodModelDO.getBeginTime();
                Date endTime = timePeriodModelDO.getEndTime();
                effectiveStartDate = timePeriodModelDO.getEffectiveStartDate();
                Date effectiveEndDate = timePeriodModelDO.getEffectiveEndDate();
                Date lastGeneratedDate = timePeriodModelDO.getLastGeneratedDate();
                Long id = timePeriodModelDO.getId();

                // 这里其实不需要每天定时任务，都把advanceBookingDay都生成一遍，例如今天已经生成了未来七天的时间段了，那么明天其实只需要生成第八天的时间段即可，所以使用到lastGeneratedDate
                Date generateDate = null;
                for (int i = 0; i <= 7; i++) {
                    // 获取要生成的日期
                    generateDate = new Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000);
                    if (lastGeneratedDate != null && generateDate.before(lastGeneratedDate)) {
                        // 如果对应日期的时间段已经被生成过了，直接跳过
                        continue;
                    }
                    // 检查明天的日期是否在这个范围内
                    boolean isInDateRange = generateDate.after(effectiveStartDate) && generateDate.before(effectiveEndDate);
                    if (isInDateRange) {
                        TimePeriodDO timePeriodDO = TimePeriodDO.builder()
                                .partitionId(partitionId)
                                .price(price)
                                .stock(idAndPartitionMap.get(partitionId).getNum())
                                .bookedSlots(0L)
                                .periodDate(DateUtil.dateToLocalDate(generateDate))
                                .beginTime(DateUtil.dateToLocalTime(beginTime))
                                .endTime(DateUtil.dateToLocalTime(endTime))
                                .build();
                        timePeriodDO.setId(SnowflakeIdUtil.nextId());
                        timePeriodDOList.add(timePeriodDO);
                    }
                }
                if (generateDate != null) {
                    // 批量更新时间段模版的最新生成日期
                    TimePeriodModelDO newTimePeriodModelDO = new TimePeriodModelDO();
                    newTimePeriodModelDO.setLastGeneratedDate(generateDate);

                    QueryWrapper<TimePeriodModelDO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("id", id)
                            .eq("partition_id", partitionId);

                    timePeriodModelService.update(newTimePeriodModelDO, queryWrapper);
                }
            }
            System.out.println("生成的时间段数：" + timePeriodDOList.size());
            timePeriodService.saveBatch(timePeriodDOList);
        });

    }

}




