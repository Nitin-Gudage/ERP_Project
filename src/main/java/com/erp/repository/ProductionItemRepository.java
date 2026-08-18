package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.ProductionItem;

public interface ProductionItemRepository
        extends JpaRepository<ProductionItem, Long> {

    List<ProductionItem>
    findByProductionId(Long productionId);
}