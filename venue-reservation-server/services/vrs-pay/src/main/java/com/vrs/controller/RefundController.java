package com.vrs.controller;

import com.vrs.convention.result.Result;
import com.vrs.convention.result.Results;
import com.vrs.domain.dto.req.AlipayRefundReqDTO;
import com.vrs.domain.dto.resp.AlipayRefundRespDTO;
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
@RequestMapping("/refund/")
public class RefundController {

    private final AlipayService alipayService;

    /**
     * 调用支付宝进行退款
     *
     * @param alipayRefundReqDTO
     */
    @PostMapping("/v1/alipay/commonRefund")
    @Operation(summary = "普通退款")
    public Result<AlipayRefundRespDTO> commonRefund(@RequestBody AlipayRefundReqDTO alipayRefundReqDTO) {
        AlipayRefundRespDTO refundRespDTO = alipayService.commonRefund(alipayRefundReqDTO);
        return Results.success(refundRespDTO);
    }

}
