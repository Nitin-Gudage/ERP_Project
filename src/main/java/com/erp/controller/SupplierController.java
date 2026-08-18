package com.erp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.dto.SupplierRequest;
import com.erp.entity.Supplier;
import com.erp.service.SupplierService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping
    public ResponseEntity<Supplier> create(
            @Valid @RequestBody SupplierRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(supplierService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> getAll() {

        return ResponseEntity.ok(
                supplierService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                supplierService.getById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> update(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {

        return ResponseEntity.ok(
                supplierService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.delete(id));
    }
}