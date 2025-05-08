package com.vrs.domain.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改订单为已支付状态
 *
 * @Author dam
 * @create 2024/12/1 19:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportUserExcelMqDTO {

    /**
     * 机构id，如果是机构管理员，必须填写；用户如果归属于某个机构，也要填写
     */
    private Long organizationId;

    /**
     * excel文件所在路径
     */
    private String excelPath;

}
