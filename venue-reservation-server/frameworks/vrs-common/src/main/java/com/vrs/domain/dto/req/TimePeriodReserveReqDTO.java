package com.vrs.domain.dto.req;

import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.domain.entity.VenueDO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 时间段预定请求参数
 */
@Data
public class TimePeriodReserveReqDTO {

    /**
     * 时间段ID
     */
    @NotNull
    private Long timePeriodId;

    private TimePeriodDO timePeriodDO;

    private Long venueId;

    private Integer courtIndex;

    private VenueDO venueDO;

    private PartitionDO partitionDO;

    public TimePeriodReserveReqDTO(Long timePeriodId, Integer courtIndex) {
        this.timePeriodId = timePeriodId;
        this.courtIndex = courtIndex;
    }
}
