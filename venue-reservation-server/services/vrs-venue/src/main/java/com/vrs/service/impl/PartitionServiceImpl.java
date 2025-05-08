package com.vrs.service.impl;

import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrs.StringRedisTemplateProxy;
import com.vrs.common.constant.PictureTypeConstant;
import com.vrs.common.context.UserContext;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.constant.UserTypeConstant;
import com.vrs.convention.errorcode.BaseErrorCode;
import com.vrs.convention.exception.ClientException;
import com.vrs.convention.page.PageResponse;
import com.vrs.domain.dto.req.PartitionListReqDTO;
import com.vrs.domain.dto.req.PartitionPicDeleteReqDTO;
import com.vrs.domain.dto.req.PartitionPicUploadReqDTO;
import com.vrs.domain.dto.resp.PartitionRespDTO;
import com.vrs.domain.dto.resp.PictureItemRespDTO;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.PictureDO;
import com.vrs.enums.PartitionStatusEnum;
import com.vrs.enums.PartitionTypeEnum;
import com.vrs.mapper.PartitionMapper;
import com.vrs.service.PartitionService;
import com.vrs.service.PicService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author dam
 * @description 针对表【partition_0】的数据库操作Service实现
 * @createDate 2024-12-07 12:01:56
 */
@Service
@RequiredArgsConstructor
public class PartitionServiceImpl extends ServiceImpl<PartitionMapper, PartitionDO>
        implements PartitionService {

    @Qualifier("distributedCache")
    private final StringRedisTemplateProxy distributedCache;
    private final StringRedisTemplate redisTemplate;
    private final PicService picService;

    @Override
    public Long getVenueIdById(Long partitionId) {
        Long venueId = (Long) distributedCache.safeGet(
                String.format(RedisCacheConstant.VENUE_PARTITION_ID_TO_VENUE_ID_KEY, partitionId),
                new TypeReference<Long>() {
                },
                () -> {
                    return baseMapper.getVenueIdById(partitionId);
                },
                1,
                TimeUnit.DAYS);
        return venueId;
    }

    @Override
    public PartitionDO getPartitionDOByIdAndVenueId(Long partitionId, Long venueId) {
        QueryWrapper<PartitionDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("venue_id", venueId);
        queryWrapper.eq("id", partitionId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public PartitionDO getPartitionDOById(Long partitionId) {
        return (PartitionDO) distributedCache.safeGet(
                String.format(RedisCacheConstant.VENUE_GET_PARTITION_BY_ID_KEY, partitionId),
                new TypeReference<PartitionDO>() {
                },
                () -> {
                    // todo 场区表的分片键是场馆ID，这里会触发读扩散问题，需要使用基因算法优化
                    return this.getById(partitionId);
                },
                1,
                TimeUnit.DAYS);
    }

    @Override
    public PageResponse<PartitionRespDTO> pagePartitionsDO(PartitionListReqDTO request) {
        QueryWrapper<PartitionDO> queryWrapper = new QueryWrapper<>();
        if (request.getVenueId() != null) {
            queryWrapper.eq("venue_id", request.getVenueId());
        }
        IPage<PartitionDO> page = baseMapper.selectPage(new Page(request.getCurrent(), request.getSize()), queryWrapper);

        List<Long> partitionIdList = page.getRecords().stream().map(item -> {
            return item.getId();
        }).toList();
        List<PartitionRespDTO> partitionRespDTOList = new ArrayList<>();
        if (partitionIdList.size() > 0) {
            List<PictureDO> pictureDOList = picService.list(new QueryWrapper<PictureDO>().in("item_id", partitionIdList));
            partitionRespDTOList = page.getRecords().stream().map(item -> {
                PartitionRespDTO partitionRespDTO = new PartitionRespDTO();
                BeanUtils.copyProperties(item, partitionRespDTO);
                partitionRespDTO.setTypeName(PartitionTypeEnum.findValueByType(item.getType()));
                partitionRespDTO.setStatusName(PartitionStatusEnum.findValueByType(item.getStatus()));
                partitionRespDTO.setPictureList(new ArrayList<>());
                for (PictureDO pictureDO : pictureDOList) {
                    if (pictureDO.getItemId().equals(partitionRespDTO.getId())) {
                        partitionRespDTO.getPictureList().add(PictureItemRespDTO.builder()
                                .id(pictureDO.getId())
                                .itemId(pictureDO.getItemId())
                                .url(pictureDO.getPicture())
                                .build());
                    }
                }
                return partitionRespDTO;
            }).collect(Collectors.toList());
        }

        return new PageResponse(request.getCurrent(), request.getSize(), page.getTotal(), partitionRespDTOList);
    }

    @Override
    public void savePicList(PartitionPicUploadReqDTO partitionPicUploadReqDTO) {
        picService.savePicList(partitionPicUploadReqDTO.getPartitionId(), partitionPicUploadReqDTO.getPictureList(), PictureTypeConstant.PARTITION);
        // 先修改数据库，再删除缓存
        redisTemplate.delete(String.format(RedisCacheConstant.VENUE_GET_PARTITION_BY_ID_KEY, partitionPicUploadReqDTO.getPartitionId()));
    }

    @Override
    public void removePicture(PartitionPicDeleteReqDTO partitionPicDeleteReqDTO) {
        picService.deletePicList(partitionPicDeleteReqDTO.getPartitionId(), partitionPicDeleteReqDTO.getPictureItemIdList(), PictureTypeConstant.PARTITION);
        // 先修改数据库，再删除缓存
        redisTemplate.delete(String.format(RedisCacheConstant.VENUE_GET_PARTITION_BY_ID_KEY, partitionPicDeleteReqDTO.getPartitionId()));
    }

    @Override
    public List<Map<String, Object>> listPartitionType(String keyword) {
        return PartitionTypeEnum.findEnumsInfoByKeyword(keyword);
    }

    @Override
    public void validateUserType() {
        if (!UserTypeConstant.validateBiggerThanVenueManager(UserContext.getUserType())){
            throw new ClientException(BaseErrorCode.USER_TYPE_IS_NOT_RIGHT_ERROR);
        }
    }

    @Override
    public List<PartitionDO> listPartitionWithIdList(List<Long> partitionIdList, List<Long> venueIdList) {
        if (partitionIdList.size() == 0) {
            return new ArrayList<>();
        } else {
            QueryWrapper<PartitionDO> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", partitionIdList);
            queryWrapper.in("venue_id", venueIdList);
            return baseMapper.selectList(queryWrapper);
        }
    }

}




