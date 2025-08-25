package com.synapse.computationengine.streams;

import com.synapse.computationengine.model.EnrichedTick;
import com.synapse.computationengine.model.PriceAggregate;
import com.synapse.computationengine.model.StockTick;
import com.synapse.computationengine.model.TradingSignal;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafkaStreams
public class MovingAverageStreamProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MovingAverageStreamProcessor.class);
    private static final String MARKET_DATA_TOPIC = "market-data";
    private static final String TRADING_SIGNALS_TOPIC = "trading-signals";
    private static final BigDecimal SIGNAL_THRESHOLD = new BigDecimal("0.005"); // 0.5%

    private <T> Serde<T> createJsonSerde(Class<T> targetType) {
        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("spring.json.value.default.type", targetType.getName());
        serdeProps.put("spring.json.trusted.packages", "com.synapse.computationengine.model,com.synapse.marketdataingestor.model");
        JsonSerde<T> serde = new JsonSerde<>(targetType);
        serde.configure(serdeProps, false);
        return serde;
    }

    @Bean
    public KStream<String, StockTick> processMarketData(StreamsBuilder streamsBuilder) {
        // Create Serdes for all our data types
        Serde<StockTick> stockTickSerde = createJsonSerde(StockTick.class);
        Serde<PriceAggregate> priceAggregateSerde = createJsonSerde(PriceAggregate.class);
        Serde<TradingSignal> tradingSignalSerde = createJsonSerde(TradingSignal.class);
        Serde<BigDecimal> bigDecimalSerde = createJsonSerde(BigDecimal.class);

        // 1. SOURCE: Read from the 'market-data' topic.
        KStream<String, StockTick> sourceStream = streamsBuilder
                .stream(MARKET_DATA_TOPIC, Consumed.with(Serdes.String(), stockTickSerde));

        // 2. PROCESS (Part 1): Calculate the 5-second moving average and convert it to a new stream.
        KStream<String, BigDecimal> averageStream = sourceStream
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(5)))
                .aggregate(
                        () -> new PriceAggregate(BigDecimal.ZERO, 0L),
                        (key, tick, aggregate) -> new PriceAggregate(
                                aggregate.sum().add(tick.price()),
                                aggregate.count() + 1
                        ),
                        Materialized.<String, PriceAggregate, WindowStore<Bytes, byte[]>>as("price-aggregate-store")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(priceAggregateSerde)
                )
                .toStream()
                .map((windowedKey, aggregate) -> {
                    BigDecimal average = BigDecimal.ZERO;
                    if (aggregate != null && aggregate.count() > 0) {
                        average = aggregate.sum().divide(BigDecimal.valueOf(aggregate.count()), 2, RoundingMode.HALF_UP);
                    }
                    // Re-key the stream from Windowed<String> back to String to enable the join
                    return new KeyValue<>(windowedKey.key(), average);
                });

        // 3. PROCESS (Part 2): Join the original stream of ticks with the new stream of averages.
        ValueJoiner<StockTick, BigDecimal, EnrichedTick> joiner = EnrichedTick::new;

        sourceStream
                .join(averageStream,
                        joiner,
                        // A tick can join with an average if their timestamps are within the same 5-second window.
                        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(5)),
                        StreamJoined.with(Serdes.String(), stockTickSerde, bigDecimalSerde)
                )
                .peek((key, enrichedTick) -> LOGGER.info("Enriched Tick: Price={}, Avg={}", enrichedTick.originalTick().price(), enrichedTick.movingAverage()))

                // 4. PROCESS (Part 3): Generate signals based on the enriched data.
                .map((key, enrichedTick) -> {
                    BigDecimal price = enrichedTick.originalTick().price();
                    BigDecimal average = enrichedTick.movingAverage();
                    String signalType = "HOLD";

                    // Avoid division by zero if average is 0
                    if (average.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal difference = price.subtract(average).abs();
                        BigDecimal thresholdAmount = average.multiply(SIGNAL_THRESHOLD);

                        if (difference.compareTo(thresholdAmount) > 0) {
                            signalType = price.compareTo(average) > 0 ? "SELL" : "BUY";
                        }
                    }

                    return new KeyValue<>(key, new TradingSignal(
                        key, signalType, price, average, enrichedTick.originalTick().timestamp()
                    ));
                })
                .filter((key, signal) -> !signal.signalType().equals("HOLD"))

                // 5. SINK: Publish the final trading signals to the 'trading-signals' topic.
                .peek((key, signal) -> LOGGER.info("<<<<<----- Generated Trading Signal: {} ----->>>>>", signal))
                .to(TRADING_SIGNALS_TOPIC, Produced.with(Serdes.String(), tradingSignalSerde));

        return sourceStream;
    }
}