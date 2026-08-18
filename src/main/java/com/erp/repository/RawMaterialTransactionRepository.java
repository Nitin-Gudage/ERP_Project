package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.RawMaterialTransaction;

public interface RawMaterialTransactionRepository
        extends JpaRepository<
                RawMaterialTransaction,
                Long> {


    List<RawMaterialTransaction>
    findByRawMaterialId(
            Long rawMaterialId
    );


    List<RawMaterialTransaction>
    findByProductionId(
            Long productionId
    );


    List<RawMaterialTransaction>
    findByPurchaseId(
            Long purchaseId
    );
}