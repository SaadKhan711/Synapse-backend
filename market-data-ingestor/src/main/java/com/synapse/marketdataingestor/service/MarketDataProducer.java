package com.synapse.marketdataingestor.service;

import com.synapse.marketdataingestor.model.StockTick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for sending market data events to Kafka.
 */
@Service
public class MarketDataProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketDataProducer.class);
    private static final String TOPIC_NAME = "market-data";

    private final KafkaTemplate<String, StockTick> kafkaTemplate;

    public MarketDataProducer(KafkaTemplate<String, StockTick> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a StockTick event to the 'market-data' Kafka topic.
     * @param tick The stock tick data to send.
     */
    public void sendStockTick(StockTick tick) {
        // We use the stock symbol as the key. This ensures that all ticks for the same
        // stock go to the same partition in Kafka, maintaining order.
        kafkaTemplate.send(TOPIC_NAME, tick.symbol(), tick);
        LOGGER.info("Sent tick to Kafka -> Symbol: {}, Price: {}", tick.symbol(), tick.price());
    }
}
