package com.vrs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrs.common.context.UserContext;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.constant.UserTypeConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ClientException;
import com.vrs.convention.page.PageResponse;
import com.vrs.convention.page.PageUtil;
import com.vrs.domain.dto.req.TimePeriodModelListReqDTO;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.domain.entity.TimePeriodModelDO;
import com.vrs.domain.entity.VenueDO;
import com.vrs.mapper.TimePeriodModelMapper;
import com.vrs.service.PartitionService;
import com.vrs.service.TimePeriodModelService;
import com.vrs.service.TimePeriodService;
import com.vrs.service.VenueService;
import com.vrs.utils.DateUtil;
import com.vrs.utils.SnowflakeIdUtil;
import groovy.util.logging.Slf4j;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author dam
 * @description 针对表【time_period_model_0】的数据库操作Service实现
 * @createDate 2024-11-17 14:29:46
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TimePeriodModelServiceImpl extends ServiceImpl<TimePeriodModelMapper, TimePeriodModelDO>
        implements TimePeriodModelService {

    private final DataSource dataSource;
    private final TimePeriodService timePeriodService;
    private final StringRedisTemplate stringRedisTemplate;
    private final PartitionService partitionService;
    private final VenueService venueService;

    /**
     * 根据时间段模板生成时间段
     *
     * @param tableIndex 表索引
     */
    @Override
    public void generateTimePeriodByModel(int tableIndex, boolean isCacheTimePeriod) {
        long start = System.currentTimeMillis();

        // 一级缓存
        HashMap<Long, Integer> partitionIdAndAdvanceBookingDayMap = new HashMap<>();

        // 缓冲池
        List<TimePeriodDO> timePeriodDOInsertBatch = new ArrayList<>();
        List<TimePeriodModelDO> timePeriodDOModelUpdateBatch = new ArrayList<>();
        int batchSize = 1000;

        // 分页参数
        int pageSize = 1000;
        int currentPage = 1;
        boolean hasMore = true;

        while (hasMore) {
            // 使用baseMapper分页查询数据
            LambdaQueryWrapper<TimePeriodModelDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TimePeriodModelDO::getIsDeleted, 0)
                    .eq(TimePeriodModelDO::getStatus, 0)
                    .last("LIMIT " + (currentPage - 1) * pageSize + "," + pageSize);

            List<TimePeriodModelDO> modelList = baseMapper.selectList(queryWrapper);

            if (modelList.isEmpty()) {
                hasMore = false;
                continue;
            }

            // 遍历查询结果
            for (TimePeriodModelDO model : modelList) {
                long id = model.getId();
                long partitionId = model.getPartitionId();
                long venueId = model.getVenueId();
                Date beginTime = model.getBeginTime();
                Date endTime = model.getEndTime();
                BigDecimal price = model.getPrice();
                Date effectiveStartDate = model.getEffectiveStartDate();
                Date effectiveEndDate = model.getEffectiveEndDate();
                Date lastGeneratedDate = model.getLastGeneratedDate();

                int advanceBookingDay = getAdvanceBookingDayByPartitionId(partitionIdAndAdvanceBookingDayMap, partitionId);

                PartitionDO partitionDO = partitionService.getPartitionDOByIdAndVenueId(partitionId, venueId);
                if (partitionDO == null) {
                    continue;
                }

                // 清理缓存
                stringRedisTemplate.delete(String.format(
                        RedisCacheConstant.VENUE_TIME_PERIOD_BY_PARTITION_ID_KEY,
                        partitionId));

                // 生成未来时间段
                Date generateDate = null;
                for (int i = 1; i <= advanceBookingDay; i++) {
                    generateDate = new Date(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000);
                    if (lastGeneratedDate != null && generateDate.before(lastGeneratedDate)) {
                        continue;
                    }

                    if (generateDate.after(effectiveStartDate) && generateDate.before(effectiveEndDate)) {
                        TimePeriodDO timePeriodDO = TimePeriodDO.builder()
                                .partitionId(partitionId)
                                .price(price)
                                .stock(partitionDO.getNum())
                                .bookedSlots(0L)
                                .periodDate(DateUtil.dateToLocalDate(generateDate))
                                .beginTime(DateUtil.dateToLocalTime(beginTime))
                                .endTime(DateUtil.dateToLocalTime(endTime))
                                .build();
                        timePeriodDO.setId(SnowflakeIdUtil.nextId());
                        timePeriodDOInsertBatch.add(timePeriodDO);

                        if (timePeriodDOInsertBatch.size() >= batchSize) {
                            timePeriodService.batchPublishTimePeriod(timePeriodDOInsertBatch, isCacheTimePeriod);
                            timePeriodDOInsertBatch.clear();
                        }
                    }
                }

                if (generateDate != null) {
                    TimePeriodModelDO timePeriodModelDO = new TimePeriodModelDO();
                    timePeriodModelDO.setId(id);
                    timePeriodModelDO.setPartitionId(partitionId);
                    timePeriodModelDO.setLastGeneratedDate(generateDate);
                    timePeriodDOModelUpdateBatch.add(timePeriodModelDO);

                    if (timePeriodDOModelUpdateBatch.size() >= batchSize) {
                        this.updateLastGeneratedDateBatch(timePeriodDOModelUpdateBatch);
                        timePeriodDOModelUpdateBatch.clear();
                    }
                }
            }

            currentPage++;
        }

        // 处理剩余数据
        if (!timePeriodDOInsertBatch.isEmpty()) {
            timePeriodService.batchPublishTimePeriod(timePeriodDOInsertBatch, isCacheTimePeriod);
        }
        if (!timePeriodDOModelUpdateBatch.isEmpty()) {
            this.updateLastGeneratedDateBatch(timePeriodDOModelUpdateBatch);
        }

        log.debug("扫描时间段模板生成时间段花费时间：" + ((System.currentTimeMillis() - start) / 1000));
    }

    /**
     * 根据时间段模板生成时间段
     *
     * @param tableIndex 表索引
     */
    @Override
    @SneakyThrows
    public void generateTimePeriodByModelOptimize(int tableIndex, boolean isCacheTimePeriod) {
        // 获取 dataSource Bean 的连接
        @Cleanup Connection conn = dataSource.getConnection();
        @Cleanup Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(Integer.MIN_VALUE);

        long start = System.currentTimeMillis();
        // 查询sql，只查询关键的字段
        String sql = "SELECT id,price,partition_id,venue_id,begin_time,end_time,effective_start_date,effective_end_date,last_generated_date FROM time_period_model_" + tableIndex + " where is_deleted = 0 and status = 0";
        @Cleanup ResultSet rs = stmt.executeQuery(sql);

        // 一级缓存
        HashMap<Long, Integer> partitionIdAndAdvanceBookingDayMap = new HashMap<>();

        // 缓冲池，积累到一定的量才进行插入或修改
        List<TimePeriodDO> timePeriodDOInsertBatch = new ArrayList<>();
        List<TimePeriodModelDO> timePeriodDOModelUpdateBatch = new ArrayList<>();
        int batchSize = 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // 每次获取一行数据进行处理，rs.next()如果有数据返回true，否则返回false
        while (rs.next()) {
            // 获取数据中的属性
            long id = rs.getLong("id");
            long partitionId = rs.getLong("partition_id");
            long venueId = rs.getLong("venue_id");
            Date beginTime = sdf.parse(rs.getString("begin_time"));
            Date endTime = sdf.parse(rs.getString("end_time"));
            BigDecimal price = rs.getBigDecimal("price");
            Date effectiveStartDate = rs.getDate("effective_start_date");
            Date effectiveEndDate = rs.getDate("effective_end_date");
            // 上次生成到的日期
            Date lastGeneratedDate = rs.getDate("last_generated_date");
            int advanceBookingDay = getAdvanceBookingDayByPartitionId(partitionIdAndAdvanceBookingDayMap, partitionId);

            PartitionDO partitionDO = partitionService.getPartitionDOByIdAndVenueId(partitionId, venueId);
            if (partitionDO == null) {
                continue;
            }

            // 如果当前分区存在可预订时间的缓存，这里进行删除，因为生成了新的，需要重新查询数据库
            stringRedisTemplate.delete(String.format(
                    RedisCacheConstant.VENUE_TIME_PERIOD_BY_PARTITION_ID_KEY,
                    partitionId));

            // 这里其实不需要每天定时任务，都把advanceBookingDay都生成一遍，例如今天已经生成了未来七天的时间段了，那么明天其实只需要生成第八天的时间段即可，所以使用到lastGeneratedDate
            Date generateDate = null;
            for (int i = 1; i <= advanceBookingDay; i++) {
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
                            .stock(partitionDO.getNum())
                            .bookedSlots(0L)
                            .periodDate(DateUtil.dateToLocalDate(generateDate))
                            .beginTime(DateUtil.dateToLocalTime(beginTime))
                            .endTime(DateUtil.dateToLocalTime(endTime))
                            .build();
                    timePeriodDO.setId(SnowflakeIdUtil.nextId());
                    timePeriodDOInsertBatch.add(timePeriodDO);
                    if (timePeriodDOInsertBatch.size() >= batchSize) {
                        // --if-- 数据量够了，存储数据库
                        timePeriodService.batchPublishTimePeriodOptimize(timePeriodDOInsertBatch, isCacheTimePeriod);
                        timePeriodDOInsertBatch.clear();
                    }
                }
            }
            if (generateDate != null) {
                // 批量更新时间段模版的最新生成日期
                TimePeriodModelDO timePeriodModelDO = new TimePeriodModelDO();
                timePeriodModelDO.setId(id);
                timePeriodModelDO.setPartitionId(partitionId);
                timePeriodModelDO.setLastGeneratedDate(generateDate);
                timePeriodDOModelUpdateBatch.add(timePeriodModelDO);
                if (timePeriodDOModelUpdateBatch.size() >= batchSize) {
                    // --if-- 数据量够了，修改数据库
                    this.updateLastGeneratedDateBatch(timePeriodDOModelUpdateBatch);
                    timePeriodDOModelUpdateBatch.clear();
                }
            }
        }
        // 处理最后一波数据
        if (timePeriodDOInsertBatch.size() >= 0) {
            // 将时间段存储到数据库
            timePeriodService.batchPublishTimePeriodOptimize(timePeriodDOInsertBatch, isCacheTimePeriod);
            timePeriodDOInsertBatch.clear();
        }
        if (timePeriodDOModelUpdateBatch.size() >= 0) {
            // --if-- 数据量够了，修改数据库
            this.updateLastGeneratedDateBatch(timePeriodDOModelUpdateBatch);
            timePeriodDOModelUpdateBatch.clear();
        }
        log.debug("扫描时间段模板生成时间段花费时间：" + ((System.currentTimeMillis() - start) / 1000));
    }

    /**
     * 批量更新时间段模板的最新生成日期
     *
     * @param timePeriodDOModelUpdateBatch
     */
    private void updateLastGeneratedDateBatch(List<TimePeriodModelDO> timePeriodDOModelUpdateBatch) {
        if (timePeriodDOModelUpdateBatch == null || timePeriodDOModelUpdateBatch.size() == 0) {
            return;
        }
        baseMapper.updateLastGeneratedDateBatch(timePeriodDOModelUpdateBatch);
    }

    /**
     * 获取分区的提前预定时间
     * 使用二级缓存，本地缓存找不到，再去Redis中找，还找不到的话，去数据库中找
     *
     * @param partitionIdAndAdvanceBookingDayMap
     * @param partitionId
     * @return
     */
    private int getAdvanceBookingDayByPartitionId(HashMap<Long, Integer> partitionIdAndAdvanceBookingDayMap, long partitionId) {
        if (partitionIdAndAdvanceBookingDayMap.containsKey(partitionId)) {
            return partitionIdAndAdvanceBookingDayMap.get(partitionId);
        }
        VenueDO venueDO = venueService.getVenueDOByPartitionId(partitionId);
        partitionIdAndAdvanceBookingDayMap.put(partitionId, venueDO.getAdvanceBookingDay());
        return venueDO.getAdvanceBookingDay();
    }

    @Override
    public void insert(TimePeriodModelDO timePeriodModelDO) {
        // 判断时间是否正确
        if (timePeriodModelDO.getEndTime().before(timePeriodModelDO.getBeginTime()) ||
                timePeriodModelDO.getEndTime().equals(timePeriodModelDO.getBeginTime())) {
            throw new ClientException("当前所添加模板的时间段的结束时间不能小于或等于开始时间，请重新设置时间段");
        }
        // 判断生效时间是否正确
        if (timePeriodModelDO.getEffectiveEndDate().before(timePeriodModelDO.getEffectiveStartDate()) ||
                timePeriodModelDO.getEffectiveEndDate().equals(timePeriodModelDO.getEffectiveStartDate())) {
            throw new ClientException("当前所添加模板的生效时间的结束日期不能小于或等于开始日期，请重新设置生效日期");
        }
        // 检验新模板的时间段是否和已有模板时间段重叠
        List<TimePeriodModelDO> overlapModelList = baseMapper.selectOverlapModel(timePeriodModelDO);
        if (overlapModelList.size() > 0) {
            throw new ClientException("当前所添加模板的时间段和已有模板的时间段有所冲突，请重新设置时间段");
        }
        baseMapper.insert(timePeriodModelDO);
    }

    @Override
    public PageResponse<TimePeriodModelDO> pageTimePeriodModelDO(TimePeriodModelListReqDTO request) {
        QueryWrapper<TimePeriodModelDO> queryWrapper = new QueryWrapper<>();
        if (request.getPartitionId() != null) {
            queryWrapper.eq("partition_id", request.getPartitionId());
        }
        // 按照开始时间升序排序
        queryWrapper.orderByAsc("begin_time");
        IPage<TimePeriodModelDO> page = baseMapper.selectPage(new Page(request.getCurrent(), request.getSize()), queryWrapper);
        return PageUtil.convert(page);
    }

    @Override
    public void validateUserType() {
        if (!UserTypeConstant.validateBiggerThanVenueManager(UserContext.getUserType())) {
            throw new ClientException(BaseErrorCode.USER_TYPE_IS_NOT_RIGHT_ERROR);
        }
    }

}




