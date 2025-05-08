package com.vrs.controller;

import com.vrs.annotation.Idempotent;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.convention.page.PageResponse;
import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.VenueListReqDTO;
import com.vrs.domain.dto.req.VenuePicDeleteReqDTO;
import com.vrs.domain.dto.req.VenuePicUploadReqDTO;
import com.vrs.domain.dto.resp.VenueRespDTO;
import com.vrs.domain.entity.VenueDO;
import com.vrs.domain.validate.AddGroup;
import com.vrs.domain.validate.UpdateGroup;
import com.vrs.enums.IdempotentSceneEnum;
import com.vrs.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 场馆控制层
 */
@RestController
@RequestMapping("/venue/")
@RequiredArgsConstructor
@Tag(name = "场馆管理")
public class VenueController {

    private final VenueService venueService;
    private final StringRedisTemplate redisTemplate;

    /**
     * 增添数据
     */
    @PostMapping("/save")
    @Operation(summary = "新增场馆")
    @Idempotent(
            uniqueKeyPrefix = "vrs-venue:venue:save:",
            key = "'_'+#venueDO.hashCode()",
            message = "正在新增，请勿重复进行...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    public Result save(@Validated({AddGroup.class}) @RequestBody VenueDO venueDO) {
        venueService.insert(venueDO);
        return Results.success();
    }

    /**
     * 查询数据
     */
    @PostMapping("/list")
    @Operation(summary = "查询场馆列表")
    public Result<PageResponse<VenueRespDTO>> list(@RequestBody VenueListReqDTO request) {
        PageResponse<VenueRespDTO> list = venueService.pageVenueDO(request);
        return Results.success(list);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/removeById")
    @Operation(summary = "根据ID删除场馆")
    public Result removeById(Integer id) {
        venueService.validateUserType();
        venueService.removeById(id);
        return Results.success();
    }

    /**
     * 删除数据
     */
    @PostMapping("/removeByIds")
    @Operation(summary = "根据ID集合批量删除场馆")
    public Result removeByIds(@RequestBody List<Long> idList) {
        venueService.validateUserType();
        venueService.removeByIds(idList);
        return Results.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    @Operation(summary = "修改场馆")
    public Result update(@Validated({UpdateGroup.class}) @RequestBody VenueDO venueDO) {
        venueService.validateUserType();
        venueService.updateById(venueDO);
        // 删除场馆缓存
        redisTemplate.delete(String.format(RedisCacheConstant.VENUE_GET_VENUE_BY_ID_KEY, venueDO.getId()));
        return Results.success();
    }

    /**
     * 根据id获取场馆
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @Operation(summary = "根据ID获取场馆")
    public Result<VenueRespDTO> getById(@PathVariable Long id) {
        return Results.success(venueService.getVenueDespDTOById(id));
    }

    /**
     * 根据id获取场馆
     *
     * @param keyword
     * @return
     */
    @GetMapping("/listVenueType")
    @Operation(summary = "获取场馆类型")
    public Result listVenueType(@RequestParam(required = false) String keyword) {
        if (keyword == null) keyword = "";
        return Results.success(venueService.listVenueType(keyword));
    }

    /**
     * 上传图片
     *
     * @param partitionPicUploadReqDTO
     * @return
     */
    @PostMapping("/uploadPicture")
    @Operation(summary = "上传场馆图片")
    @Idempotent(
            uniqueKeyPrefix = "vrs-venue:partition:uploadPicture:",
            key = "'_'+#partitionPicUploadReqDTO.hashCode()",
            message = "正在上传，请勿重复进行...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    public Result uploadPicture(@Validated @RequestBody VenuePicUploadReqDTO partitionPicUploadReqDTO) {
        venueService.savePicList(partitionPicUploadReqDTO);
        // 先修改数据库，再删除缓存
        redisTemplate.delete(String.format(RedisCacheConstant.VENUE_GET_VENUE_BY_ID_KEY, partitionPicUploadReqDTO.getVenueId()));
        return Results.success();
    }

    /**
     * 删除分区图片
     */
    @PostMapping("/removePicture")
    @Operation(summary = "删除场馆图片")
    public Result removePicture(@Validated @RequestBody VenuePicDeleteReqDTO partitionPicDeleteReqDTO) {
        venueService.removePicture(partitionPicDeleteReqDTO);
        // 先修改数据库，再删除缓存
        redisTemplate.delete(String.format(RedisCacheConstant.VENUE_GET_VENUE_BY_ID_KEY, partitionPicDeleteReqDTO.getVenueId()));
        return Results.success();
    }

}
