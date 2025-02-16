package com.sky.task;

import com.sky.server.SocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PingPongTask {
    public final SocketServer server;

    /**
     * 每分钟发送一次心跳包
     */
    @Scheduled(cron = "0 * * * * *")
    private void ping() {
        log.info("Websocket心跳机制");
        server.sendToAll("ping");
    }
}
