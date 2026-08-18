package com.erp.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.RawMaterialPerProductRequest;
import com.erp.entity.RawMaterialPerProduct;
import com.erp.service.RawMaterialPerProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/raw-materials-per-product")
@RequiredArgsConstructor
public class RawMaterialPerProductController {
    private final RawMaterialPerProductService service;

    @PostMapping
    public ResponseEntity<RawMaterialPerProduct> create(@Valid @RequestBody RawMaterialPerProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RawMaterialPerProduct>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialPerProduct> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<RawMaterialPerProduct>> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getByProductId(productId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialPerProduct> update(@PathVariable Long id, @Valid @RequestBody RawMaterialPerProductRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}