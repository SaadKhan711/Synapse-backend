package com.synapse.modelservice.controller;

import com.synapse.modelservice.kafka.ModelEventProducer; // <-- IMPORT
import com.synapse.modelservice.model.FinancialModel;
import com.synapse.modelservice.repository.FinancialModelRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/models")
public class FinancialModelController {

    private final FinancialModelRepository repository;
    private final ModelEventProducer modelEventProducer; 

    public FinancialModelController(FinancialModelRepository repository, ModelEventProducer modelEventProducer) {
        this.repository = repository;
        this.modelEventProducer = modelEventProducer; 
    }

    @GetMapping
    public List<FinancialModel> getAllModels() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinancialModel> getModelById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FinancialModel createModel(@RequestBody FinancialModel model, @AuthenticationPrincipal Jwt jwt) {
        model.setOwner(jwt.getSubject());
        model.setLastModified(Instant.now());
        
        // 1. First, save the model to the database
        FinancialModel savedModel = repository.save(model);
        
        // 2. After it's successfully saved, send an event to Kafka
        modelEventProducer.sendModelCreatedEvent(savedModel);
        
        return savedModel;
    }
}