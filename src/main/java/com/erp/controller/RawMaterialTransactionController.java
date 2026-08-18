package com.erp.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.RawMaterialTransactionRequest;
import com.erp.entity.RawMaterialTransaction;
import com.erp.service.RawMaterialTransactionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/raw-material-transactions")
@RequiredArgsConstructor
public class RawMaterialTransactionController {
    private final RawMaterialTransactionService transactionService;

    @PostMapping
    public ResponseEntity<RawMaterialTransaction> create(@RequestBody RawMaterialTransactionRequest request) {
        return ResponseEntity.ok(transactionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RawMaterialTransaction>> getAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialTransaction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @GetMapping("/raw-material/{rawMaterialId}")
    public ResponseEntity<List<RawMaterialTransaction>> getByRawMaterial(@PathVariable Long rawMaterialId) {
        return ResponseEntity.ok(transactionService.getByRawMaterialId(rawMaterialId));
    }

    @GetMapping("/production/{productionId}")
    public ResponseEntity<List<RawMaterialTransaction>> getByProduction(@PathVariable Long productionId) {
        return ResponseEntity.ok(transactionService.getByProductionId(productionId));
    }

    @GetMapping("/purchase/{purchaseId}")
    public ResponseEntity<List<RawMaterialTransaction>> getByPurchase(@PathVariable Long purchaseId) {
        return ResponseEntity.ok(transactionService.getByPurchaseId(purchaseId));
    }
}