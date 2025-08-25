package com.synapse.modelservice.kafka;

import com.synapse.modelservice.model.FinancialModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * A service responsible for producing (sending) events related to financial models to Kafka.
 */
@Service
public class ModelEventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelEventProducer.class);
    private static final String TOPIC_NAME = "model-events";

    private final KafkaTemplate<String, FinancialModel> kafkaTemplate;

    public ModelEventProducer(KafkaTemplate<String, FinancialModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a "Model Created" event to the 'model-events' Kafka topic.
     * @param model The financial model that was just created.
     */
    public void sendModelCreatedEvent(FinancialModel model) {
        LOGGER.info("Sending ModelCreatedEvent to Kafka topic: {}", TOPIC_NAME);
        try {
            // The key of the message is the model's ID, which helps with partitioning.
            // The value is the full FinancialModel object, which will be serialized to JSON.
            kafkaTemplate.send(TOPIC_NAME, model.getId().toString(), model);
            LOGGER.info("Successfully sent event for model ID: {}", model.getId());
        } catch (Exception e) {
            LOGGER.error("Failed to send ModelCreatedEvent for model ID: {}", model.getId(), e);
        }
    }
}