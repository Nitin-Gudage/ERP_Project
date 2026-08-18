package com.erp.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.GoodsStockRequest;
import com.erp.entity.GoodsStock;
import com.erp.service.GoodsStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/goods-stock")
@RequiredArgsConstructor
public class GoodsStockController {
    private final GoodsStockService goodsStockService;

    @PostMapping
    public ResponseEntity<GoodsStock> create(@Valid @RequestBody GoodsStockRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goodsStockService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<GoodsStock>> getAll() {
        return ResponseEntity.ok(goodsStockService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsStock> getById(@PathVariable Long id) {
        return ResponseEntity.ok(goodsStockService.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<GoodsStock> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(goodsStockService.getByProductId(productId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoodsStock> update(@PathVariable Long id, @Valid @RequestBody GoodsStockRequest request) {
        return ResponseEntity.ok(goodsStockService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(goodsStockService.delete(id));
    }
}