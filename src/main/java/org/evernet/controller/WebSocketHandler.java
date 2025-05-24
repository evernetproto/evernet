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

    private static final Map<String, Set<WebSocketSession>> actorSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String actorAddress = (String) session.getAttributes().get("actorAddress");

        if (actorAddress != null) {
            actorSessions.computeIfAbsent(actorAddress, k -> new CopyOnWriteArraySet<>()).add(session);
            log.info("Session added for actor {}", actorAddress);
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
        String actorAddress = (String) session.getAttributes().get("actorAddress");
        if (actorAddress != null && actorSessions.containsKey(actorAddress)) {
            actorSessions.get(actorAddress).remove(session);
            if (actorSessions.get(actorAddress).isEmpty()) {
                actorSessions.remove(actorAddress);
            }
            log.info("Session closed for actor {}", actorAddress);
        }
    }

    public static void sendToActor(String actorAddress, String message) throws Exception {
        Set<WebSocketSession> sessions = actorSessions.get(actorAddress);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            }
        }
    }
}
