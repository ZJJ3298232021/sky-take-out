package com.sky.result;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SocketResult {
    // 消息内容
    private String content;
    // 1表示下单消息推送， 2表示催单消息推送
    private Integer type;
    // 订单id
    private Long orderId;
}
