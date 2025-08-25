package com.synapse.tradingengine.kafka;

import com.synapse.tradingengine.model.TradingSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * This service listens for trading signals and simulates their execution.
 */
@Service
public class TradingSignalConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradingSignalConsumer.class);

    /**
     * Listens for messages on the 'trading-signals' topic.
     * @param signal The TradingSignal object received from Kafka.
     */
    @KafkaListener(topics = "trading-signals", groupId = "trading-engine-group")
    public void consumeTradingSignal(TradingSignal signal) {
        LOGGER.info("<<<<<----- Received Trading Signal ----->>>>>");
        LOGGER.info("EXECUTING {} ORDER for symbol {} at price {}",
                signal.signalType(),
                signal.symbol(),
                signal.triggerPrice()
        );
        // In a real system, this is where you would connect to a brokerage API
        // to place the actual trade.
    }
}