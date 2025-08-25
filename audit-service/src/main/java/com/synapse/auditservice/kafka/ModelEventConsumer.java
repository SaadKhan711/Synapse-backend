package com.synapse.auditservice.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * A service that consumes events from Kafka topics.
 */
@Service
public class ModelEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelEventConsumer.class);

    /**
     * Listens for messages on the 'model-events' topic.
     * The `groupId` ensures that if we scale this service up, only one instance
     * in the group will receive a particular message.
     *
     * @param event The message received from Kafka. Spring Boot will automatically
     * deserialize the JSON message into a String.
     */
    @KafkaListener(topics = "model-events", groupId = "audit-service-group")
    public void consumeModelEvent(String event) {
        LOGGER.info("<<<<<----- AUDIT LOG: Received event from 'model-events' topic ----->>>>>");
        LOGGER.info("Event Details: {}", event);
        // In the future, we would save this event to an immutable database like Amazon QLDB.
    }
}
