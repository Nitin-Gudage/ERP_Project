package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.GoodsTransaction;

public interface GoodsTransactionRepository
        extends JpaRepository<GoodsTransaction, Long> {

    List<GoodsTransaction>
    findByProductId(Long productId);

    List<GoodsTransaction>
    findByProductionId(Long productionId);
}