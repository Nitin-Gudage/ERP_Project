package com.erp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.GoodsStock;

public interface GoodsStockRepository
        extends JpaRepository<GoodsStock, Long> {

    Optional<GoodsStock>
    findByProductId(Long productId);
}