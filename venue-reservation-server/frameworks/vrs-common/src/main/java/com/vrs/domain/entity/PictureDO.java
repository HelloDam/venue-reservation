package com.vrs.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrs.domain.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 
 * @TableName picture
 */
@TableName(value ="picture")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PictureDO extends BaseEntity implements Serializable {
    /**
     * 项目ID
     */
    private Long itemId;

    /**
     * 图片
     */
    private String picture;

    /**
     * 项目类型 0：场馆图片 1：分区图片 2：评论图片
     */
    private Integer itemType;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}