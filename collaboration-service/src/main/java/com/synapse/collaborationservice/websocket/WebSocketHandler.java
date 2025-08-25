package com.synapse.collaborationservice.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages all active WebSocket connections and broadcasts messages.
 * This class is the central hub for real-time communication with the frontend.
 */
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    // A thread-safe list to hold all active client sessions.
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        LOGGER.info("New WebSocket connection established: {}", session.getId());
        LOGGER.info("Total active sessions: {}", sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        LOGGER.info("WebSocket connection closed: {}. Reason: {}", session.getId(), status);
        LOGGER.info("Total active sessions: {}", sessions.size());
    }

    /**
     * This method is called by our Kafka consumer. It takes an event payload (as a JSON string)
     * and sends it to every single connected client.
     * @param eventPayload The message from a Kafka topic.
     */
    public void broadcast(String eventPayload) {
        TextMessage message = new TextMessage(eventPayload);
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                LOGGER.error("Failed to send message to session {}: {}", session.getId(), e.getMessage());
            }
        }
    }
}