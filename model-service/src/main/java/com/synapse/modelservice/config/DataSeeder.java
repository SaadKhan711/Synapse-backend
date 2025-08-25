package com.synapse.modelservice.config;

import com.synapse.modelservice.model.FinancialModel;
import com.synapse.modelservice.repository.FinancialModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * This component runs on application startup and seeds the database with
 * initial data if it's empty.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSeeder.class);
    private final FinancialModelRepository financialModelRepository;

    public DataSeeder(FinancialModelRepository financialModelRepository) {
        this.financialModelRepository = financialModelRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if the database already has data
        if (financialModelRepository.count() == 0) {
            LOGGER.info("Database is empty. Seeding with initial models...");

            // Create Model 1
            FinancialModel model1 = new FinancialModel();
            model1.setName("Q3 Earnings Forecast");
            model1.setOwner("system-generated"); // No real user at startup
            model1.setContent("{\"version\": 1}");
            model1.setLastModified(Instant.now());

            // Create Model 2
            FinancialModel model2 = new FinancialModel();
            model2.setName("Arbitrage Strategy v1.2");
            model2.setOwner("system-generated");
            model2.setContent("{\"strategy\": \"ETH/BTC\"}");
            model2.setLastModified(Instant.now());

            // Save both models to the database
            financialModelRepository.saveAll(List.of(model1, model2));

            LOGGER.info("Successfully seeded 2 models into the database.");
        } else {
            LOGGER.info("Database already contains data. Skipping seeding.");
        }
    }
}
