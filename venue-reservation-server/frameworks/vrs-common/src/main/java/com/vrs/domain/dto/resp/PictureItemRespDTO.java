package com.vrs.domain.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author dam
 * @create 2024/12/29 15:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PictureItemRespDTO {
    private Long id;
    private Long itemId;
    private String url;
}
