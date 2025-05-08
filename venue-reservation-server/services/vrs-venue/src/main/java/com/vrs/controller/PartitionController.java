package com.vrs.controller;

import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.starter.annotation.LogRecord;
import com.vrs.annotation.Idempotent;
import com.vrs.constant.RedisCacheConstant;
import com.vrs.convention.page.PageResponse;
import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.PartitionListReqDTO;
import com.vrs.domain.dto.req.PartitionPicDeleteReqDTO;
import com.vrs.domain.dto.req.PartitionPicUploadReqDTO;
import com.vrs.domain.dto.resp.PartitionRespDTO;
import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.validate.AddGroup;
import com.vrs.enums.IdempotentSceneEnum;
import com.vrs.service.PartitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 场区控制层
 */
@RestController
@RequestMapping("/venue/partition/")
@RequiredArgsConstructor
@Tag(name = "分区管理")
public class PartitionController {

    private final PartitionService partitionService;
    private final StringRedisTemplate redisTemplate;

    /**
     * 增添数据
     */
    @PostMapping("/save")
    @LogRecord(
            bizNo = "{{#id}}",
            type = "新增分区",
            subType = "{{T(com.vrs.common.context.UserContext).getUserType()}}",
            success = """
                    场馆ID：{{#partitionDO.venueId}}， \
                    分区名称：{{#partitionDO.name}}， \
                    分区类型：{VenueTypeEnumParse{#partitionDO.type}}， \
                    描述：{{#partitionDO.description}}， \
                    场区拥有的场数量：{{#partitionDO.num}}， \
                    场区状态：{PartitionStatusEnumParse{#partitionDO.type}}; \
                    结果:{{#_ret}}
                    """,
            fail = "接口调用失败，失败原因：{{#_errorMsg}}",
            extra = "{{#partitionDO.toString()}}",
            operator = "{{T(com.vrs.common.context.UserContext).getUsername()}}"
    )
    @Idempotent(
            uniqueKeyPrefix = "vrs-venue:partition:save:",
            key = "'_'+#partitionDO.hashCode()",
            message = "正在新增，请勿重复进行...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    @Operation(summary = "新增分区")
    public Result save(@Validated({AddGroup.class}) @RequestBody PartitionDO partitionDO) {
        partitionService.validateUserType();
        partitionService.save(partitionDO);
        // 因为 ID 是存储到数据库中才生成的，@LogRecord 默认拿不到，需要我们将信息手动设置到上下文中
        LogRecordContext.putVariable("id", partitionDO.getId());
        return Results.success();
    }

    /**
     * 查询数据
     */
    @PostMapping("/list")
    @Operation(summary = "查询分区列表")
    public Result<PageResponse<PartitionRespDTO>> list(@Validated @RequestBody PartitionListReqDTO request) {
        PageResponse<PartitionRespDTO> list = partitionService.pagePartitionsDO(request);
        return Results.success(list);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/removeById")
    @Operation(summary = "根据ID删除分区")
    public Result removeById(Integer id) {
        partitionService.removeById(id);
        return Results.success();
    }

    /**
     * 删除数据
     */
    @PostMapping("/removeByIds")
    @Operation(summary = "根据ID集合批量删除分区")
    public Result removeByIds(@RequestBody List<Long> idList) {
        partitionService.removeByIds(idList);
        return Results.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    @Operation(summary = "修改分区")
    public Result update(@RequestBody PartitionDO partitionDO) {
        partitionService.updateById(partitionDO);
        // 先修改数据库，再删除缓存
        redisTemplate.delete(String.format(RedisCacheConstant.VENUE_GET_PARTITION_BY_ID_KEY, partitionDO.getId()));
        return Results.success();
    }

    /**
     * 根据id获取数据源
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @Operation(summary = "根据ID获取分区")
    public Result getById(@PathVariable Long id) {
        return Results.success(partitionService.getById(id));
    }

    /**
     * 上传图片
     *
     * @param partitionPicUploadReqDTO
     * @return
     */
    @PostMapping("/uploadPicture")
    @Operation(summary = "上传分区图片")
    @Idempotent(
            uniqueKeyPrefix = "vrs-venue:partition:uploadPicture:",
            key = "'_'+#partitionPicUploadReqDTO.hashCode()",
            message = "正在上传，请勿重复进行...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    public Result uploadPicture(@Validated @RequestBody PartitionPicUploadReqDTO partitionPicUploadReqDTO) {
        partitionService.savePicList(partitionPicUploadReqDTO);
        return Results.success();
    }

    /**
     * 删除分区图片
     */
    @PostMapping("/removePicture")
    @Operation(summary = "删除分区图片")
    public Result removePicture(@Validated @RequestBody PartitionPicDeleteReqDTO partitionPicDeleteReqDTO) {
        partitionService.removePicture(partitionPicDeleteReqDTO);
        return Results.success();
    }

    /**
     * 根据id获取数据源
     *
     * @param keyword
     * @return
     */
    @GetMapping("/listPartitionType")
    @Operation(summary = "获取分区类型")
    public Result listPartitionType(@RequestParam(required = false) String keyword) {
        if (keyword == null) keyword = "";
        return Results.success(partitionService.listPartitionType(keyword));
    }
}
