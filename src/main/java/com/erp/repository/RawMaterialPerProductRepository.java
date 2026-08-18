package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.RawMaterialPerProduct;

public interface RawMaterialPerProductRepository
        extends JpaRepository<RawMaterialPerProduct, Long> {

    List<RawMaterialPerProduct> findByProductId(Long productId);
}