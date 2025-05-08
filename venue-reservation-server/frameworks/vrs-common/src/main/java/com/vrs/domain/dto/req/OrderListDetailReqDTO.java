package com.vrs.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author dam
 * @create 2024/12/7 10:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderListDetailReqDTO {
    private List<Long> venueIdList;
    private List<Long> partitionIdList;
}
