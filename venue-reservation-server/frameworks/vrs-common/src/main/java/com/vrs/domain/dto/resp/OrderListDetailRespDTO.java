package com.vrs.domain.dto.resp;

import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.TimePeriodDO;
import com.vrs.domain.entity.VenueDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author dam
 * @create 2024/12/7 10:51
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderListDetailRespDTO implements Serializable {
    private List<VenueDO> venueDOList;
    private List<PartitionDO> partitionDOList;
    private List<TimePeriodDO> timePeriodDOList;
    private static final long serialVersionUID = 1L;
}
