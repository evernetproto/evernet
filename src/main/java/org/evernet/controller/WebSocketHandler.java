package org.evernet.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userAddress = (String) session.getAttributes().get("userAddress");

        if (userAddress != null) {
            userSessions.computeIfAbsent(userAddress, k -> new CopyOnWriteArraySet<>()).add(session);
            log.info("Session added for user {}", userAddress);
        } else {
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        session.sendMessage(new TextMessage("Server received: " + message.getPayload()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userAddress = (String) session.getAttributes().get("userAddress");
        if (userAddress != null && userSessions.containsKey(userAddress)) {
            userSessions.get(userAddress).remove(session);
            if (userSessions.get(userAddress).isEmpty()) {
                userSessions.remove(userAddress);
            }
            log.info("Session closed for user {}", userAddress);
        }
    }

    public static void sendToUser(String userAddress, String message) throws Exception {
        Set<WebSocketSession> sessions = userSessions.get(userAddress);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }
}