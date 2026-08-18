package com.erp.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.SalesOrderDetailsResponse;
import com.erp.dto.SalesOrderRequest;
import com.erp.entity.SalesOrder;
import com.erp.service.SalesOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sales-orders")
@RequiredArgsConstructor
public class SalesOrderController {
    private final SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<SalesOrder> create(@Valid @RequestBody SalesOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salesOrderService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<SalesOrder>> getAll() {
        return ResponseEntity.ok(salesOrderService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderDetailsResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.getOrderDetails(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesOrder> update(@PathVariable Long id, @Valid @RequestBody SalesOrderRequest request) {
        return ResponseEntity.ok(salesOrderService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(salesOrderService.delete(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SalesOrder> changeStatus(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(salesOrderService.changeStatus(id, status, userId));
    }
}