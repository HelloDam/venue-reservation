package com.vrs.domain.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.vrs.domain.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 场区
 *
 * @TableName partition
 */
@Data
public class PartitionRespDTO extends BaseEntity implements Serializable {

    /**
     * 场馆ID
     */
    private Long venueId;

    /**
     * 分区名称
     */
    private String name;

    /**
     * 分区类型 1:篮球 2:足球 3：羽毛球 4:排球
     */
    private Integer type;

    private String typeName;

    /**
     * 描述，如是否提供器材等等
     */
    private String description;

    /**
     * 场区拥有的场数量
     */
    private Integer num;

    /**
     * 场区状态 0：关闭 1：开放 2：维护中
     */
    private Integer status;

    private String statusName;

    /**
     * 场区图片
     */
    private List<PictureItemRespDTO> pictureList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}