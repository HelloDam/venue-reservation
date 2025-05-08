package com.vrs.controller;

import com.vrs.annotation.Idempotent;
import com.vrs.convention.page.PageResponse;
import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.TimePeriodModelListReqDTO;
import com.vrs.domain.entity.TimePeriodModelDO;
import com.vrs.domain.validate.AddGroup;
import com.vrs.enums.IdempotentSceneEnum;
import com.vrs.service.TimePeriodModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制层
 */
@RestController
@RequestMapping("/venue/time-period-model/")
@RequiredArgsConstructor
@Tag(name = "时间段模板管理")
public class TimePeriodModelController {

    private final TimePeriodModelService timePeriodModelService;

    /**
     * 增添数据
     */
    @PostMapping("/v1/save")
    @Operation(summary = "新增时间段模板")
    @Idempotent(
            uniqueKeyPrefix = "vrs-venue:time-period-model:save:",
            key = "'_'+#timePeriodModelDO.hashCode()",
            message = "正在新增，请勿重复进行...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    public Result save(@Validated({AddGroup.class}) @RequestBody TimePeriodModelDO timePeriodModelDO) {
        timePeriodModelService.validateUserType();
        timePeriodModelService.insert(timePeriodModelDO);
        return Results.success();
    }

    /**
     * 查询数据
     */
    @PostMapping("/list")
    @Operation(summary = "查询时间段模板列表")
    public Result<PageResponse<TimePeriodModelDO>> list(@Validated @RequestBody TimePeriodModelListReqDTO request) {
        PageResponse<TimePeriodModelDO> list = timePeriodModelService.pageTimePeriodModelDO(request);
        return Results.success(list);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/removeById")
    @Operation(summary = "根据ID删除时间段模板")
    public Result removeById(Integer id) {
        timePeriodModelService.validateUserType();
        timePeriodModelService.removeById(id);
        return Results.success();
    }

    /**
     * 删除数据
     */
    @PostMapping("/removeByIds")
    @Operation(summary = "根据ID集合批量删除时间段模板")
    public Result removeByIds(@RequestBody List<Long> idList) {
        timePeriodModelService.validateUserType();
        timePeriodModelService.removeByIds(idList);
        return Results.success();
    }

    /**
     * 修改数据
     */
    @PostMapping("/update")
    @Operation(summary = "修改时间段模板")
    public Result update(@RequestBody TimePeriodModelDO timePeriodModelDO) {
        timePeriodModelService.validateUserType();
        timePeriodModelService.updateById(timePeriodModelDO);
        return Results.success();
    }

    /**
     * 根据id获取数据源
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @Operation(summary = "根据ID获取时间段模板")
    public Result getById(@PathVariable Long id) {
        return Results.success(timePeriodModelService.getById(id));
    }

}
