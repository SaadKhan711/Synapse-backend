package com.synapse.computationengine.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a single stock price update.
 * IMPORTANT: This class structure MUST exactly match the StockTick record
 * in the market-data-ingestor service for deserialization to work correctly.
 */
public record StockTick(
    String symbol,
    BigDecimal price,
    Instant timestamp
) {}
