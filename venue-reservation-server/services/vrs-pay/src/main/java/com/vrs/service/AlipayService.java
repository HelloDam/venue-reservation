package com.vrs.service;

import com.vrs.common.dto.PayCallbackDTO;
import com.vrs.domain.dto.req.AlipayInfoReqDTO;
import com.vrs.domain.dto.req.AlipayPayReqDTO;
import com.vrs.domain.dto.req.AlipayRefundReqDTO;
import com.vrs.domain.dto.resp.AlipayInfoRespDTO;
import com.vrs.domain.dto.resp.AlipayRefundRespDTO;

/**
 * @Author dam
 * @create 2024/12/31 14:11
 */
public interface AlipayService {
    String commonPay(AlipayPayReqDTO alipayPayReqDTO);

    void callback(PayCallbackDTO payCallbackDTO);

    AlipayRefundRespDTO commonRefund(AlipayRefundReqDTO alipayRefundReqDTO);

    AlipayInfoRespDTO info(AlipayInfoReqDTO alipayInfoReqDTO);
}
