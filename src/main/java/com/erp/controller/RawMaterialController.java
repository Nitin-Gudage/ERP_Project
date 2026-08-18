package com.erp.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.RawMaterialRequest;
import com.erp.entity.RawMaterial;
import com.erp.service.RawMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
public class RawMaterialController {
    private final RawMaterialService rawMaterialService;

    @PostMapping
    public ResponseEntity<RawMaterial> create(@Valid @RequestBody RawMaterialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rawMaterialService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<RawMaterial>> getAll() {
        return ResponseEntity.ok(rawMaterialService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterial> getById(@PathVariable Long id) {
        return ResponseEntity.ok(rawMaterialService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterial> update(@PathVariable Long id, @Valid @RequestBody RawMaterialRequest request) {
        return ResponseEntity.ok(rawMaterialService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(rawMaterialService.delete(id));
    }
}