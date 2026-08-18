package com.erp.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.GoodsTransactionRequest;
import com.erp.entity.GoodsTransaction;
import com.erp.service.GoodsTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/goods-transactions")
@RequiredArgsConstructor
public class GoodsTransactionController {
    private final GoodsTransactionService transactionService;

    @PostMapping
    public ResponseEntity<GoodsTransaction> create(@Valid @RequestBody GoodsTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<GoodsTransaction>> getAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsTransaction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<GoodsTransaction>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(transactionService.getByProductId(productId));
    }

    @GetMapping("/production/{productionId}")
    public ResponseEntity<List<GoodsTransaction>> getByProductionId(@PathVariable Long productionId) {
        return ResponseEntity.ok(transactionService.getByProductionId(productionId));
    }
}