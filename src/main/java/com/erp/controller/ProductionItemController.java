package com.erp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.dto.ProductionItemRequest;
import com.erp.entity.ProductionItem;
import com.erp.service.ProductionItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/production-items")
@RequiredArgsConstructor
public class ProductionItemController {

    private final ProductionItemService itemService;

    // CREATE
    @PostMapping
    public ResponseEntity<ProductionItem> create(
            @Valid
            @RequestBody
            ProductionItemRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        itemService.create(request)
                );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<ProductionItem>>
    getAll() {

        return ResponseEntity.ok(
                itemService.getAll()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductionItem>
    getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                itemService.getById(id)
        );
    }

    // GET ITEMS BY PRODUCTION
    @GetMapping("/production/{productionId}")
    public ResponseEntity<List<ProductionItem>>
    getByProductionId(
            @PathVariable Long productionId) {

        return ResponseEntity.ok(
                itemService.getByProductionId(
                        productionId
                )
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ProductionItem>
    update(
            @PathVariable Long id,
            @Valid
            @RequestBody
            ProductionItemRequest request) {

        return ResponseEntity.ok(
                itemService.update(
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.delete(id));
    }
}