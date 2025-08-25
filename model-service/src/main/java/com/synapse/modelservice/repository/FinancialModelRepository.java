package com.synapse.modelservice.repository;

import com.synapse.modelservice.model.FinancialModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for FinancialModel entities.
 * This interface provides all standard CRUD (Create, Read, Update, Delete) operations
 * without needing to write any implementation code.
 */
@Repository
public interface FinancialModelRepository extends JpaRepository<FinancialModel, Long> {
    // You can add custom query methods here if needed, e.g.,:
    // List<FinancialModel> findByOwner(String owner);
}