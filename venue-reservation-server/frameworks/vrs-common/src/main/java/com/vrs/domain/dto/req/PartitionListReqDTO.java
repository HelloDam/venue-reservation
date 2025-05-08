package com.vrs.domain.dto.req;

import com.vrs.convention.page.PageRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dam
 * @create 2024/12/7 13:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartitionListReqDTO extends PageRequest {
    @NotNull(message = "场馆ID不可以为空")
    private Long venueId;
}
