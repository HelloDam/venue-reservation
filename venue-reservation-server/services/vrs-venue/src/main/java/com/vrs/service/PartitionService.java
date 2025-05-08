package com.vrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrs.convention.page.PageResponse;
import com.vrs.domain.dto.req.PartitionListReqDTO;
import com.vrs.domain.dto.req.PartitionPicDeleteReqDTO;
import com.vrs.domain.dto.req.PartitionPicUploadReqDTO;
import com.vrs.domain.dto.resp.PartitionRespDTO;
import com.vrs.domain.entity.PartitionDO;

import java.util.List;
import java.util.Map;

/**
 * @author dam
 * @description 针对表【partition_0】的数据库操作Service
 * @createDate 2024-12-07 12:01:56
 */
public interface PartitionService extends IService<PartitionDO> {

    /**
     * 根据分区ID获取场馆ID
     * @param partitionId
     * @return
     */
    Long getVenueIdById(Long partitionId);

    PartitionDO getPartitionDOById(Long partitionId);

    PartitionDO getPartitionDOByIdAndVenueId(Long partitionId,Long venueId);

    PageResponse<PartitionRespDTO> pagePartitionsDO(PartitionListReqDTO request);

    void savePicList(PartitionPicUploadReqDTO partitionPicUploadReqDTO);

    void removePicture(PartitionPicDeleteReqDTO partitionPicDeleteReqDTO);

    List<PartitionDO> listPartitionWithIdList(List<Long> partitionIdList, List<Long> venueIdList);

    List<Map<String, Object>>  listPartitionType(String keyword);

    void validateUserType();
}
