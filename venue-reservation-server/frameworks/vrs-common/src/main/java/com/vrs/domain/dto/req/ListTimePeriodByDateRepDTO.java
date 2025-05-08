package com.vrs.domain.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @Author dam
 * @create 2024/12/28 21:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListTimePeriodByDateRepDTO {
    @NotNull(message = "分区ID不能为空")
    private Long partitionId;
    @NotNull(message = "日期不能为空")
    private LocalDate date;
}
