package com.synapse.marketdataingestor.service;

import com.synapse.marketdataingestor.model.StockTick;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux; 

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

/**
 * This component simulates a live feed of stock prices.
 * It starts generating data as soon as the application is running.
 */
@Component
public class StockPriceSimulator {

    private final MarketDataProducer marketDataProducer;
    private BigDecimal currentPrice = new BigDecimal("150.00");
    private final Random random = new Random();

    public StockPriceSimulator(MarketDataProducer marketDataProducer) {
        this.marketDataProducer = marketDataProducer;
    }

    /**
     * The @PostConstruct annotation ensures this method is run automatically
     * after the component has been initialized.
     */
    @PostConstruct
    public void startSimulation() {
        // This is the core of the reactive stream.
        Flux.interval(Duration.ofSeconds(1)) // 1. Emit a new event every 1 second.
            .map(tick -> generateRandomStockTick()) // 2. For each event, generate a new stock tick.
            .subscribe(marketDataProducer::sendStockTick); // 3. For each new tick, send it to Kafka.
    }

    /**
     * Generates a new stock tick with a slightly randomized price.
     * @return A new StockTick object.
     */
    private StockTick generateRandomStockTick() {
        // Generate a small positive or negative change
        double change = (random.nextDouble() - 0.5) * 2; // a value between -1.0 and +1.0
        currentPrice = currentPrice.add(new BigDecimal(change));
        currentPrice = currentPrice.setScale(2, RoundingMode.HALF_UP); // Keep 2 decimal places

        return new StockTick("SYNAPSE", currentPrice, Instant.now());
    }
}