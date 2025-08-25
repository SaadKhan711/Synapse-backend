package com.synapse.computationengine.model;

import java.math.BigDecimal;

/**
 * A helper record to store the state of our aggregation.
 * It holds the running sum of prices and the count of ticks.
 */
public record PriceAggregate(
    BigDecimal sum,
    long count
) {}