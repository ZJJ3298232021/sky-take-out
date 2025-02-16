package com.sky.server;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author zank
 * @description: websocket服务端,用于实现催单/下单播报
 */

@Component
@ServerEndpoint("/ws/{cid}")
@Slf4j
public class SocketServer {

    public static final HashMap<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接建立成功调用
     * @param session 会话
     * @param cid 客户端id
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("cid") String cid) {
        log.info("客户端{}连接成功", cid);
        sessionMap.put(cid, session);
    }

    /**
     * 连接关闭调用
     * @param cid 客户端id
     */
    @OnClose
    public void onClose(@PathParam("cid") String cid) {
        log.info("客户端{}连接关闭", cid);
        sessionMap.remove(cid);
    }

    /**
     * 处理由于网络错误、超时、服务器端异常等异常时调用
     * @param throwable 异常
     * @param session 会话
     */
    @OnError
    public void onError(Throwable throwable, Session session) {
        log.error("客户端{}发生错误", session, throwable);
        if(session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("关闭连接失败", e);
            }
        }
    }

    /**
     * 发送消息给所有客户端
     * @param message 消息
     */
    public void sendToAll(String message) {
        for (Session session : sessionMap.values()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("发送消息失败", e);
            }
        }
    }
}
