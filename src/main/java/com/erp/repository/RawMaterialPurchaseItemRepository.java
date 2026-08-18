package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.RawMaterialPurchaseItem;

public interface RawMaterialPurchaseItemRepository
        extends JpaRepository<
                RawMaterialPurchaseItem,
                Long> {

    List<RawMaterialPurchaseItem>
    findByPurchaseId(Long purchaseId);
}