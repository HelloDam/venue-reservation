package com.vrs.domain.dto.resp;

import com.vrs.domain.entity.PartitionDO;
import com.vrs.domain.entity.VenueDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author dam
 * @create 2024/12/7 10:51
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRespDTO implements Serializable {
    private VenueDO venueDO;
    private PartitionDO partitionDO;
    private static final long serialVersionUID = 1L;
}
