package com.vrs.domain.base;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.vrs.domain.validate.UpdateGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @Author dam
 * @create 2024/1/19 16:29
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {
    /**
     * 使用雪花算法来生成ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "id", hidden = true)
    @NotNull(message = "id不能为空", groups = {UpdateGroup.class})
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "createTime", hidden = true)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "updateTime", hidden = true)
    private Date updateTime;

    /**
     * 逻辑删除 0：没删除 1：已删除
     */
    @TableLogic
    @Schema(description = "isDeleted", hidden = true)
    private Integer isDeleted;
}
