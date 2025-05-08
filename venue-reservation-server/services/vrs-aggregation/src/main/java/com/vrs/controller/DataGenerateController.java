package com.vrs.controller;

import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.VenueNearGenerateDTO;
import com.vrs.service.DataGenerateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

/**
 * 场馆控制层
 */
@RestController
@RequestMapping("/venue/")
@RequiredArgsConstructor
@Tag(name = "数据生成")
public class DataGenerateController {

    private final DataGenerateService generateService;
    private final StringRedisTemplate redisTemplate;

    /**
     * 生成附近场馆数据
     */
    @PostMapping("/generateNearVenue")
    @Operation(summary = "生成附近场馆数据")
    public Result generateNearVenue(@RequestBody VenueNearGenerateDTO request) throws ParseException {
        generateService.generateNearVenue(request);
        return Results.success();
    }

}
