package com.erp.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.erp.dto.HistoryRequest;
import com.erp.entity.History;
import com.erp.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @PostMapping
    public ResponseEntity<History> create(@Valid @RequestBody HistoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(historyService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<History>> getAll() {
        return ResponseEntity.ok(historyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<History> getById(@PathVariable Long id) {
        return ResponseEntity.ok(historyService.getById(id));
    }

    @GetMapping("/reference")
    public ResponseEntity<List<History>> getByReference(@RequestParam String type, @RequestParam Long id) {
        return ResponseEntity.ok(historyService.getByReference(type, id));
    }

    @GetMapping("/action")
    public ResponseEntity<List<History>> getByAction(@RequestParam String action) {
        return ResponseEntity.ok(historyService.getByAction(action));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<History>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(historyService.getByUser(userId));
    }
}