package com.erp.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.RawMaterialPurchaseRequest;
import com.erp.entity.RawMaterialPurchase;
import com.erp.entity.RawMaterialPurchaseItem;
import com.erp.service.RawMaterialPurchaseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/raw-material-purchases")
@RequiredArgsConstructor
public class RawMaterialPurchaseController {
    private final RawMaterialPurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<RawMaterialPurchase> create(@RequestBody RawMaterialPurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RawMaterialPurchase>> getAll() {
        return ResponseEntity.ok(purchaseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialPurchase> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getById(id));
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<RawMaterialPurchaseItem>> getItems(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.getItems(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<RawMaterialPurchase> changeStatus(@PathVariable Long id, @RequestParam String status, @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(purchaseService.changeStatus(id, status, userId));
    }
}