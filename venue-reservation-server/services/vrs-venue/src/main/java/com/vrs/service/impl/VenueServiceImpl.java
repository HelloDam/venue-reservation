package com.vrs.service.impl;

import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrs.StringRedisTemplateProxy;
import com.vrs.common.constant.PictureTypeConstant;
import com.vrs.common.context.UserContext;
import com.vrs.common.utils.DistanceUtil;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.constant.UserTypeConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ClientException;
import com.vrs.convention.exception.ServiceException;
import com.vrs.convention.page.PageResponse;
import com.vrs.domain.dto.req.VenueListReqDTO;
import com.vrs.domain.dto.req.VenuePicDeleteReqDTO;
import com.vrs.domain.dto.req.VenuePicUploadReqDTO;
import com.vrs.domain.dto.resp.PictureItemRespDTO;
import com.vrs.domain.dto.resp.VenueRespDTO;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.PictureDO;
import com.vrs.domain.entity.VenueDO;
import com.vrs.enums.VenueStatusEnum;
import com.vrs.enums.VenueTypeEnum;
import com.vrs.mapper.VenuesMapper;
import com.vrs.service.PartitionService;
import com.vrs.service.PicService;
import com.vrs.service.VenueService;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author dam
 * @description 针对表【venue】的数据库操作Service实现
 * @createDate 2024-12-07 10:44:56
 */
@Service
@RequiredArgsConstructor
public class VenueServiceImpl extends ServiceImpl<VenuesMapper, VenueDO>
        implements VenueService {

    @Qualifier("distributedCache")
    private final StringRedisTemplateProxy distributedCache;
    private final PartitionService partitionService;
    private final StringRedisTemplate redisTemplate;
    private final DataSource dataSource;
    private final PicService picService;

    @Override
    public VenueDO getVenueDOById(Long venueId) {
        return (VenueDO) distributedCache.safeGet(
                String.format(RedisCacheConstant.VENUE_GET_VENUE_BY_ID_KEY, venueId),
                new TypeReference<VenueDO>() {
                },
                () -> {
                    return this.getById(venueId);
                },
                1,
                TimeUnit.DAYS);
    }

    @Override
    public PageResponse<VenueRespDTO> pageVenueDO(VenueListReqDTO request) {
        List<Long> venueIdList = null;
        if (request.getLatitude() != null && request.getLongitude() != null) {
            // 先去缓存中，把位置靠近的场馆ID查询出来
            venueIdList = this.findVenuesWithinRadius(request.getLongitude(), request.getLatitude(), request.getKm());
        } else {
            venueIdList = null;
        }
        LambdaQueryWrapper<VenueDO> queryWrapper = Wrappers.lambdaQuery(VenueDO.class);
        // 只查询附近的场馆
        if (venueIdList == null || venueIdList.size() == 0) {
            // --if-- 不存在附近场馆，直接返回结果
            return new PageResponse(request.getCurrent(), request.getSize(), 0L, null);
        }
        // 根据名字模糊查询
        if (!StringUtils.isBlank(request.getName())) {
            queryWrapper.like(VenueDO::getName, "%" + request.getName() + "%");
        }
        // 根据类型查询
        if (request.getType() != null) {
            queryWrapper.eq(VenueDO::getType, request.getType());
        }
        // 根据状态查询
        if (request.getStatus() != null) {
            queryWrapper.eq(VenueDO::getStatus, request.getStatus());
        }
        // 查询对方开放场馆，或者相同机构的场馆
        queryWrapper.in(VenueDO::getId, venueIdList)
                // 要不场馆和用户处于同一机构，要么需要场馆是对外开放的
                .and(wrapper -> wrapper.eq(VenueDO::getOrganizationId, UserContext.getOrganizationId()).or().eq(VenueDO::getIsOpen, 1));
        List<VenueDO> venueDOList = baseMapper.selectList(queryWrapper);

        // 处理查询出来的数据
        List<VenueRespDTO> totalVenueRespDTOList = new ArrayList<>();
        for (VenueDO venueDO : venueDOList) {
            VenueRespDTO venueRespDTO = new VenueRespDTO();
            BeanUtils.copyProperties(venueDO, venueRespDTO);
            venueRespDTO.setTypeName(VenueTypeEnum.findValueByType(venueDO.getType()));
            venueRespDTO.setStatusName(VenueStatusEnum.findValueByType(venueDO.getStatus()));
            venueRespDTO.setPictureList(new ArrayList<>());
            // 计算距离并设置到 DTO 中
            if (request.getLatitude() != null && request.getLongitude() != null) {
                double distance = DistanceUtil.calculateDistance(
                        request.getLatitude().doubleValue(),
                        request.getLongitude().doubleValue(),
                        venueDO.getLatitude().doubleValue(),
                        venueDO.getLongitude().doubleValue()
                );
                venueRespDTO.setDistance(distance);
            }
            totalVenueRespDTOList.add(venueRespDTO);
        }
        // 按照距离升序排序
        Collections.sort(totalVenueRespDTOList, ((o1, o2) -> {
            return Double.compare(o1.getDistance(), o2.getDistance());
        }));

        // 内存分页
        int totalSize = totalVenueRespDTOList.size();
        int fromIndex = (int) ((request.getCurrent()) * request.getSize());
        int toIndex = Math.min(fromIndex + (int) (long) request.getSize(), totalSize);

        if (fromIndex >= totalSize) {
            return new PageResponse<>(request.getCurrent(), request.getSize(), (long) totalSize, Collections.emptyList());
        }

        List<VenueRespDTO> pagedList = totalVenueRespDTOList.subList(fromIndex, toIndex);

        // 查询场馆图片
        List<Long> partitionIdList = pagedList.stream().map(item -> {
            return item.getId();
        }).toList();
        if (partitionIdList.size() > 0) {
            List<PictureDO> pictureDOList = picService.list(new QueryWrapper<PictureDO>().in("item_id", partitionIdList));
            for (VenueRespDTO venueRespDTO : pagedList) {
                for (PictureDO pictureDO : pictureDOList) {
                    if (pictureDO.getItemId().equals(venueRespDTO.getId())) {
                        venueRespDTO.getPictureList().add(PictureItemRespDTO.builder()
                                .id(pictureDO.getId())
                                .itemId(pictureDO.getItemId())
                                .url(pictureDO.getPicture())
                                .build());
                    }
                }
            }
        }

        return new PageResponse(request.getCurrent(), request.getSize(), totalVenueRespDTOList.size() + 0L, pagedList);
    }

    @Override
    public VenueRespDTO getVenueDespDTOById(Long id) {
        VenueDO venueDO = baseMapper.selectById(id);
        if (venueDO == null) throw new ClientException(BaseErrorCode.VENUE_NULL_ERROR);
        VenueRespDTO venueRespDTO = new VenueRespDTO();
        BeanUtils.copyProperties(venueDO, venueRespDTO);
        venueRespDTO.setTypeName(VenueTypeEnum.findValueByType(venueDO.getType()));
        venueRespDTO.setStatusName(VenueStatusEnum.findValueByType(venueDO.getStatus()));
        venueRespDTO.setPictureList(new ArrayList<>());
        List<PictureDO> pictureDOList = picService.list(new QueryWrapper<PictureDO>().eq("item_id", venueDO.getId()));
        for (PictureDO pictureDO : pictureDOList) {
            venueRespDTO.getPictureList().add(PictureItemRespDTO.builder()
                    .id(pictureDO.getId())
                    .itemId(pictureDO.getItemId())
                    .url(pictureDO.getPicture())
                    .build());
        }
        return venueRespDTO;
    }

    /**
     * 根据经纬度和半径（公里）查询附近的场馆 ID
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param radiusKm  半径（公里）
     * @return 附近的场馆 ID 列表
     */
    public List<Long> findVenuesWithinRadius(BigDecimal longitude, BigDecimal latitude, double radiusKm) {
        // 获取 GeoOperations
        GeoOperations<String, String> geoOps = redisTemplate.opsForGeo();

        // 定义查询的中心点和半径
        Point center = new Point(longitude.doubleValue(), latitude.doubleValue());
        Distance distance = new Distance(radiusKm, Metrics.KILOMETERS);
        Circle circle = new Circle(center, distance);
        // 执行地理空间查询
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(
                // Redis 中的 key
                RedisCacheConstant.VENUE_LOCATION_KEY,
                circle
        );

        // 提取场馆 ID 并返回
        return results.getContent().stream()
                .map(result ->
                        {
                            Long venueId = Long.parseLong(result.getContent().getName());
//                            double venueDistance = result.getDistance().getValue();
//                            System.out.println("场馆 ID: " + venueId + ", 距离: " + venueDistance + " 公里");
                            return venueId;
                        }
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<VenueDO> listVenueWithIdList(List<Long> venueIdList) {
        if (venueIdList.size() == 0) {
            return new ArrayList<>();
        } else {
            return baseMapper.selectList(new QueryWrapper<VenueDO>().in("id", venueIdList));
        }
    }

    @Override
    public VenueDO getVenueDOByPartitionId(long partitionId) {
        Long venueId = (Long) distributedCache.safeGet(
                String.format(RedisCacheConstant.VENUE_GET_VENUE_ID_BY_PARTITION_ID_KEY, partitionId),
                new TypeReference<Long>() {
                },
                () -> {
                    PartitionDO partitionDOById = partitionService.getPartitionDOById(partitionId);
                    return partitionDOById.getVenueId();
                },
                1,
                TimeUnit.DAYS);
        return this.getVenueDOById(venueId);
    }

    @Override
    public List<Map<String, Object>> listVenueType(String keyword) {
        return VenueTypeEnum.findEnumsInfoByKeyword(keyword);
    }

    @Override
    public void validateUserType() {
        if (!UserTypeConstant.validateBiggerThanInstituteManager(UserContext.getUserType())) {
            throw new ClientException(BaseErrorCode.USER_TYPE_IS_NOT_RIGHT_ERROR);
        }
    }

    /**
     * 将场馆的位置信息存储到 Redis 中
     */
    @Override
    @SneakyThrows
    public void cacheVenueLocations() {

        // 获取 dataSource Bean 的连接
        @Cleanup Connection conn = dataSource.getConnection();
        @Cleanup Statement stmt = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(Integer.MIN_VALUE);
        // 查询sql，只查询关键的字段
        String sql = "SELECT id,latitude,longitude FROM venue where is_deleted = 0";

        @Cleanup ResultSet rs = stmt.executeQuery(sql);

        // 每次获取一行数据进行处理，rs.next()如果有数据返回true，否则返回false
        List<VenueDO> buffer = new ArrayList<>();
        int bufferSize = 1000;
        while (rs.next()) {
            // 获取数据中的属性
            VenueDO venueDO = new VenueDO();
            venueDO.setId(rs.getLong("id"));
            venueDO.setLongitude(rs.getBigDecimal("longitude"));
            venueDO.setLatitude(rs.getBigDecimal("latitude"));
            buffer.add(venueDO);
            if (buffer.size() >= bufferSize) {
                cacheLocations(buffer);
                buffer.clear();
            }
        }
        if (buffer.size() >= 0) {
            cacheLocations(buffer);
            buffer.clear();
        }
    }

    /**
     * 使用 Redis 管道将场馆位置添加到 Redis 缓存中
     *
     * @param buffer
     */
    public void cacheLocations(List<VenueDO> buffer) {
        // 使用 Redis 管道批量操作
        redisTemplate.executePipelined((RedisCallback<?>) (connection) -> {
            for (VenueDO venue : buffer) {
                // 确保经纬度信息不为空
                if (venue.getLongitude() != null && venue.getLatitude() != null) {
                    // 将场馆的经纬度信息存储到 Redis 中
                    Point point = new Point(venue.getLongitude().doubleValue(), venue.getLatitude().doubleValue());
                    connection.geoAdd(RedisCacheConstant.VENUE_LOCATION_KEY.getBytes(), point, venue.getId().toString().getBytes());
                }
            }
            // 管道操作不需要返回值
            return null;
        });
    }

    @Override
    public void insert(VenueDO venueDO) {
        this.validateUserType();
        try {
            baseMapper.insert(venueDO);
            // 将场馆位置缓存起来
            Point point = new Point(venueDO.getLongitude().doubleValue(), venueDO.getLatitude().doubleValue());
            redisTemplate.opsForGeo().add(RedisCacheConstant.VENUE_LOCATION_KEY, point, venueDO.getId().toString());
        } catch (Exception e) {
            throw new ServiceException(e.getMessage(), BaseErrorCode.SERVICE_ERROR);
        }
    }

    @Override
    public void savePicList(VenuePicUploadReqDTO venuePicUploadReqDTO) {
        picService.savePicList(venuePicUploadReqDTO.getVenueId(), venuePicUploadReqDTO.getPictureList(), PictureTypeConstant.VENUE);
        // 先修改数据库，再删除缓存
        redisTemplate.delete(String.format(RedisCacheConstant.VENUE_GET_PARTITION_BY_ID_KEY, venuePicUploadReqDTO.getVenueId()));
    }

    @Override
    public void removePicture(VenuePicDeleteReqDTO venuePicDeleteReqDTO) {
        picService.deletePicList(venuePicDeleteReqDTO.getVenueId(), venuePicDeleteReqDTO.getPictureItemIdList(), PictureTypeConstant.VENUE);
        // 先修改数据库，再删除缓存
        redisTemplate.delete(String.format(RedisCacheConstant.VENUE_GET_PARTITION_BY_ID_KEY, venuePicDeleteReqDTO.getVenueId()));
    }
}




