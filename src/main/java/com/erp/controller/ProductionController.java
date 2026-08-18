package com.erp.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.ProductionDetailsResponse;
import com.erp.dto.ProductionRequest;
import com.erp.entity.Production;
import com.erp.service.ProductionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productions")
@RequiredArgsConstructor
public class ProductionController {
    private final ProductionService productionService;

    @PostMapping
    public ResponseEntity<Production> create(@Valid @RequestBody ProductionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Production>> getAll() {
        return ResponseEntity.ok(productionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductionDetailsResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productionService.getProductionDetails(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Production> update(@PathVariable Long id, @Valid @RequestBody ProductionRequest request) {
        return ResponseEntity.ok(productionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(productionService.delete(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Production> changeStatus(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(productionService.changeStatus(id, status, userId));
    }
}