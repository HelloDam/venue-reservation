package com.vrs.controller;

import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.AlipayInfoReqDTO;
import com.vrs.domain.dto.req.AlipayPayReqDTO;
import com.vrs.domain.dto.resp.AlipayInfoRespDTO;
import com.vrs.service.AlipayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author dam
 * @create 2024/12/31 14:06
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "支付相关")
@RequestMapping("/pay/")
public class PayController {

    private final AlipayService alipayService;

    /**
     * 调用支付宝进行支付
     * @param alipayPayReqDTO
     * @return 支付地址
     */
    @PostMapping("/v1/alipay/commonPay")
    @Operation(summary = "普通支付")
    public Result<String> commonPay(@RequestBody AlipayPayReqDTO alipayPayReqDTO) {
        String alipayUrl = alipayService.commonPay(alipayPayReqDTO);
        return Results.success(alipayUrl);
    }

    /**
     * 查询支付宝交易详情
     * @param alipayInfoReqDTO
     * @return 支付地址
     */
    @PostMapping("/v1/alipay/info")
    @Operation(summary = "交易查询")
    public Result<AlipayInfoRespDTO> info(@RequestBody AlipayInfoReqDTO alipayInfoReqDTO) {
        return Results.success(alipayService.info(alipayInfoReqDTO));
    }

}
