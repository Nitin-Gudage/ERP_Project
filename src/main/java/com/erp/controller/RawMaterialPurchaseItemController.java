package com.erp.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.RawMaterialPurchaseItemRequest;
import com.erp.entity.RawMaterialPurchaseItem;
import com.erp.service.RawMaterialPurchaseItemService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/raw-material-purchase-items")
@RequiredArgsConstructor
public class RawMaterialPurchaseItemController {
    private final RawMaterialPurchaseItemService itemService;

    @PostMapping
    public ResponseEntity<RawMaterialPurchaseItem> create(@RequestBody RawMaterialPurchaseItemRequest request) {
        return ResponseEntity.ok(itemService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RawMaterialPurchaseItem>> getAll() {
        return ResponseEntity.ok(itemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialPurchaseItem> getById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getById(id));
    }

    @GetMapping("/purchase/{purchaseId}")
    public ResponseEntity<List<RawMaterialPurchaseItem>> getByPurchaseId(@PathVariable Long purchaseId) {
        return ResponseEntity.ok(itemService.getByPurchaseId(purchaseId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialPurchaseItem> update(@PathVariable Long id, @RequestBody RawMaterialPurchaseItemRequest request) {
        return ResponseEntity.ok(itemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.delete(id));
    }
}