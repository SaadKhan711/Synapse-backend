package com.synapse.tradingengine.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a BUY or SELL signal.
 * This MUST match the structure of the TradingSignal in the computation-engine.
 */
public record TradingSignal(
    String symbol,
    String signalType,
    BigDecimal triggerPrice,
    BigDecimal movingAverage,
    Instant timestamp
) {}