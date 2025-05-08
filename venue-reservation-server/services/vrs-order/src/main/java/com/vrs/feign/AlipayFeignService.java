package com.vrs.feign;

import com.vrs.convention.result.Result;
import com.vrs.domain.dto.req.AlipayInfoReqDTO;
import com.vrs.domain.dto.req.AlipayPayReqDTO;
import com.vrs.domain.dto.req.AlipayRefundReqDTO;
import com.vrs.domain.dto.resp.AlipayInfoRespDTO;
import com.vrs.domain.dto.resp.AlipayRefundRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author dam
 * @create 2024/12/1 16:19
 */
@FeignClient(value = "vrs-pay", url = "${aggregation.remote-url:}")
public interface AlipayFeignService {

    @PostMapping("/pay/v1/alipay/commonPay")
    public Result<String> commonPay(AlipayPayReqDTO alipayPayReqDTO);

    @PostMapping("/pay/v1/alipay/info")
    public Result<AlipayInfoRespDTO> info(@RequestBody AlipayInfoReqDTO alipayInfoReqDTO);

    @PostMapping("/refund/v1/alipay/commonRefund")
    public Result<AlipayRefundRespDTO> commonRefund(AlipayRefundReqDTO alipayRefundReqDTO);

}
