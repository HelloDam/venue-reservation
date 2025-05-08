package com.vrs.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.vrs.common.dto.PayCallbackDTO;
import com.vrs.service.AlipayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 支付结果回调
 *
 * @Author dam
 * @create 2024/12/31 15:41
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "支付相关")
public class PayCallbackController {

    private final AlipayService alipayService;

    /**
     * 支付宝回调
     * 调用支付宝支付后，支付宝会调用此接口发送支付结果
     */
    // todo 如何校验回调接口的调用是否为支付宝调用
    @PostMapping("/pay/callback/alipay")
    public void callbackAlipay(@RequestParam Map<String, Object> requestParam) {
        PayCallbackDTO payCallbackDTO = BeanUtil.mapToBean(requestParam, PayCallbackDTO.class, true, CopyOptions.create());
        alipayService.callback(payCallbackDTO);
    }
}
