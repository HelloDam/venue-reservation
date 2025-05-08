package com.vrs.domain.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用来接收canal发送过来的消息的数据
 *
 * @Author dam
 * @create 2024/12/10 14:11
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebsocketMqDTO {
    private String toUsername;
    private String message;
}
