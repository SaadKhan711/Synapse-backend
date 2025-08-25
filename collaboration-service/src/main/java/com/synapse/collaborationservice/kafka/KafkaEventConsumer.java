package com.synapse.collaborationservice.kafka;

import com.synapse.collaborationservice.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Listens to Kafka topics and forwards messages to the WebSocket handler for broadcasting.
 */
@Service
public class KafkaEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventConsumer.class);
    private final WebSocketHandler webSocketHandler;

    public KafkaEventConsumer(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    /**
     * Listens to both 'market-data' and 'trading-signals' topics.
     * @param message The raw JSON message from either topic.
     */
    @KafkaListener(topics = {"market-data", "trading-signals"}, groupId = "collaboration-service-group")
    public void consumeEvents(String message) {
        LOGGER.info("Consumed event from Kafka, broadcasting to clients -> {}", message);
        webSocketHandler.broadcast(message);
    }
}