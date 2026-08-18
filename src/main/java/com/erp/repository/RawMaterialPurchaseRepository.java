package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.RawMaterialPurchase;
import com.erp.entity.RawMaterialPurchaseStatus;

public interface RawMaterialPurchaseRepository
        extends JpaRepository<RawMaterialPurchase, Long> {

    List<RawMaterialPurchase>
    findByStatus(
            RawMaterialPurchaseStatus status
    );
}