package com.vrs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrs.convention.page.PageResponse;
import com.vrs.domain.dto.req.VenueListReqDTO;
import com.vrs.domain.dto.req.VenuePicDeleteReqDTO;
import com.vrs.domain.dto.req.VenuePicUploadReqDTO;
import com.vrs.domain.dto.resp.VenueRespDTO;
import com.vrs.domain.entity.VenueDO;

import java.util.List;
import java.util.Map;

/**
 * @author dam
 * @description 针对表【venue】的数据库操作Service
 * @createDate 2024-12-07 10:44:56
 */
public interface VenueService extends IService<VenueDO> {
    VenueDO getVenueDOById(Long venueId);

    PageResponse<VenueRespDTO> pageVenueDO(VenueListReqDTO request);

    List<VenueDO> listVenueWithIdList(List<Long> venueIdList);

    VenueDO getVenueDOByPartitionId(long partitionId);

    List<Map<String, Object>> listVenueType(String keyword);

    void validateUserType();

    void cacheVenueLocations();

    void cacheLocations(List<VenueDO> buffer);

    void insert(VenueDO venueDO);

    void savePicList(VenuePicUploadReqDTO venuePicUploadReqDTO);

    void removePicture(VenuePicDeleteReqDTO venuePicDeleteReqDTO);

    VenueRespDTO getVenueDespDTOById(Long id);
}
