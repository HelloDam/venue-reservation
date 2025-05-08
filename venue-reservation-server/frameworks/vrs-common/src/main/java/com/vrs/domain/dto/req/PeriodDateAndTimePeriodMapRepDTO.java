package com.vrs.domain.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dam
 * @create 2024/12/28 21:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeriodDateAndTimePeriodMapRepDTO {
    @NotNull(message = "分区ID不能为空")
    private Long partitionId;
}
