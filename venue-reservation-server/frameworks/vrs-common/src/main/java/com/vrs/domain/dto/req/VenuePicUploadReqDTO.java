package com.vrs.domain.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author dam
 * @create 2024/12/7 13:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VenuePicUploadReqDTO {
    @NotNull(message = "分区ID不可以为空")
    private Long venueId;

    @Size(min = 1, message = "至少上传一个图片")
    private List<String> pictureList;
}
