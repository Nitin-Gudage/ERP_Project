package com.erp.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.SalesOrderItemRequest;
import com.erp.entity.SalesOrderItem;
import com.erp.service.SalesOrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sales-order-items")
@RequiredArgsConstructor
public class SalesOrderItemController {
    private final SalesOrderItemService itemService;

    @PostMapping
    public ResponseEntity<SalesOrderItem> create(@Valid @RequestBody SalesOrderItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SalesOrderItem>> getAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderItem> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @GetMapping("/order/{salesOrderId}")
    public ResponseEntity<List<SalesOrderItem>> getBySalesOrderId(@PathVariable Long salesOrderId) {
        return ResponseEntity.ok(itemService.getBySalesOrderId(salesOrderId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesOrderItem> update(@PathVariable Long id, @Valid @RequestBody SalesOrderItemRequest request) {
        return ResponseEntity.ok(itemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.delete(id));
    }
}