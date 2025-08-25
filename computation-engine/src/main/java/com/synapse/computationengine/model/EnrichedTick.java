package com.synapse.computationengine.model;

import java.math.BigDecimal;

/**
 * Represents a StockTick that has been enriched with its corresponding moving average.
 */
public record EnrichedTick(
    StockTick originalTick,
    BigDecimal movingAverage
) {}
