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
 * 本地消息表
 * @TableName local_message
 */
@TableName(value ="local_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocalMessageDO extends BaseEntity implements Serializable {

    /**
     * 唯一消息ID
     */
    private String msgId;

    /**
     * 消息Topic
     */
    private String topic;

    /**
     * 消息Tag
     */
    private String tag;

    /**
     * 消息内容(JSON格式)
     */
    private String content;

    /**
     * 消息状态 0:待发送 1:已发送 2:消费失败
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 已重试次数
     */
    private Integer retryCount;

    /**
     * 下次重试时间戳(毫秒)
     */
    private Long nextRetryTime;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}