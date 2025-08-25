package com.synapse.marketdataingestor.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * A simple record to represent a single stock price update (a "tick").
 * Records are a modern, concise way to create immutable data objects in Java.
 */
public record StockTick(
    String symbol,
    BigDecimal price,
    Instant timestamp
) {}
