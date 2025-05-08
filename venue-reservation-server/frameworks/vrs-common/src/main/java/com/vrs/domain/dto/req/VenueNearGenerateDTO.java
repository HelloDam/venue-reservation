package com.vrs.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dam
 * @create 2024/12/7 10:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenueNearGenerateDTO {
    /**
     * 维度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;
}
