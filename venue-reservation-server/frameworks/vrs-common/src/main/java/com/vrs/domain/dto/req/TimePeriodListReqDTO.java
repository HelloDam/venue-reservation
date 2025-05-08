package com.vrs.domain.dto.req;

import com.vrs.convention.page.PageRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dam
 * @create 2024/11/17 14:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimePeriodListReqDTO extends PageRequest {
    @NotNull(message = "场区ID不可以为空")
    private Long partitionId;
}
