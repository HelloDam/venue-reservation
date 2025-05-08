package com.vrs.controller;

import com.vrs.annotation.Idempotent;
import com.vrs.convention.page.PageResponse;
import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.ListTimePeriodByDateRepDTO;
import com.vrs.domain.dto.req.PeriodDateAndTimePeriodMapRepDTO;
import com.vrs.domain.dto.req.TimePeriodListReqDTO;
import com.vrs.domain.dto.req.TimePeriodStockRestoreReqDTO;
import com.vrs.domain.dto.resp.TimePeriodRespDTO;
import com.vrs.domain.entity.OrderDO;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.enums.IdempotentSceneEnum;
import com.vrs.service.TimePeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 时间段控制层
 */
@RestController
@RequestMapping("/venue/time-period/")
@RequiredArgsConstructor
@Tag(name = "时间段")
public class TimePeriodController {

    private final TimePeriodService timePeriodService;

    /**
     * 预定时间段
     */
    @GetMapping("/v1/reserve")
    @Idempotent(
            uniqueKeyPrefix = "vrs-venue:lock_reserve:",
            // 让用户同时最多只能预定一个时间段，根据用户名来加锁
            // key = "T(com.vrs.common.context.UserContext).getUsername()",
            // 让用户同时最多只能预定该时间段一次，但是可以同时预定其他时间段，根据用户名+时间段ID来加锁
            key = "T(com.vrs.common.context.UserContext).getUsername()+'_'+#timePeriodId",
            message = "正在执行场馆预定流程，请勿重复预定...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    @Operation(summary = "预定时间段V1")
    public Result reserve1(@RequestParam("timePeriodId") Long timePeriodId, @RequestParam(value = "courtIndex", required = false, defaultValue = "-1") Integer courtIndex) {
        OrderDO orderDO = timePeriodService.reserve1(timePeriodId, courtIndex);
        return Results.success(orderDO);
    }

    /**
     * 预定时间段
     */
    @GetMapping("/v2/reserve")
    @Idempotent(
            uniqueKeyPrefix = "vrs-venue:lock_reserve:",
            // 让用户同时最多只能预定一个时间段，根据用户名来加锁
            // key = "T(com.vrs.common.context.UserContext).getUsername()",
            // 让用户同时最多只能预定该时间段一次，但是可以同时预定其他时间段，根据用户名+时间段ID来加锁
            key = "T(com.vrs.common.context.UserContext).getUsername()+'_'+#timePeriodId",
            message = "正在执行场馆预定流程，请勿重复预定...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    @Operation(summary = "预定时间段V2")
    public Result reserve2(@RequestParam("timePeriodId") Long timePeriodId, @RequestParam(value = "courtIndex", required = false, defaultValue = "-1") Integer courtIndex) {
        String orderSn = timePeriodService.reserve2(timePeriodId, courtIndex);
        return Results.success(orderSn);
    }

    /**
     * 时间段库存回退
     */
    @PostMapping("/v1/release")
    @Idempotent(
            uniqueKeyPrefix = "vrs-venue:lock_release:",
            // 让用户同时最多只能预定一个时间段，根据用户名来加锁
            // key = "T(com.vrs.common.context.UserContext).getUsername()",
            // 让用户同时最多只能预定该时间段一次，但是可以同时预定其他时间段，根据用户名+时间段ID来加锁
            key = "#timePeriodStockRestoreReqDTO.getUserId()+'_'+#timePeriodStockRestoreReqDTO.getTimePeriodId()",
            message = "正在执库存回退流程，请勿重复回退...",
            scene = IdempotentSceneEnum.RESTAPI
    )
    @Operation(summary = "回滚库存")
    public Result release(@Validated @RequestBody TimePeriodStockRestoreReqDTO timePeriodStockRestoreReqDTO) {
        timePeriodService.restoreStockAndBookedSlots(timePeriodStockRestoreReqDTO);
        return Results.success();
    }

    /**
     * 查询可预订时间段列表
     */
    @PostMapping("/getPeriodDateAndTimePeriodMap")
    @Operation(summary = "查询可预订时间段列表")
    public Result<LinkedHashMap<String, List<TimePeriodRespDTO>>> getPeriodDateAndTimePeriodMap(@Validated @RequestBody PeriodDateAndTimePeriodMapRepDTO periodDateAndTimePeriodMapRepDTO) {
        LinkedHashMap<String, List<TimePeriodRespDTO>> periodDateAndTimePeriodMap = timePeriodService.getPeriodDateAndTimePeriodMap(periodDateAndTimePeriodMapRepDTO);
        return Results.success(periodDateAndTimePeriodMap);
    }

    /**
     * 根据日期查询可预订时间段列表
     */
    @PostMapping("/listTimePeriodByDate")
    @Operation(summary = "根据日期查询可预订时间段列表")
    public Result<List<TimePeriodRespDTO>> listTimePeriodByDate(@Validated @RequestBody ListTimePeriodByDateRepDTO listTimePeriodByDateRepDTO) {
        List<TimePeriodRespDTO> timePeriodRespDTOList = timePeriodService.listTimePeriodByDate(listTimePeriodByDateRepDTO);
        return Results.success(timePeriodRespDTOList);
    }

    /**
     * 查询数据
     */
    @PostMapping("/list")
    @Operation(summary = "查询时间段列表")
    public Result<PageResponse<TimePeriodDO>> list(@Validated @RequestBody TimePeriodListReqDTO request) {
        PageResponse<TimePeriodDO> list = timePeriodService.pageTimePeriodDO(request);
        return Results.success(list);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/removeById")
    @Operation(summary = "根据ID删除时间段")
    public Result removeById(Integer id) {
        timePeriodService.removeById(id);
        return Results.success();
    }

    /**
     * 删除数据
     */
    @PostMapping("/removeByIds")
    @Operation(summary = "根据ID集合批量删除时间段")
    public Result removeByIds(@RequestBody List<Long> idList) {
        timePeriodService.removeByIds(idList);
        return Results.success();
    }

    /**
     * 修改数据
     */
//    @PostMapping("/update")
//    @Operation(summary = "修改时间段")
//    public Result update(@RequestBody TimePeriodDO timePeriodDO) {
//        timePeriodService.updateById(timePeriodDO);
//        return Results.success();
//    }

    /**
     * 根据id获取数据源
     *
     * @param id
     * @return
     */
    @GetMapping("/getById/{id}")
    @Operation(summary = "根据ID获取时间段")
    public Result<TimePeriodRespDTO> getById(@PathVariable Long id) {
        return Results.success(timePeriodService.infoById(id));
    }

}
