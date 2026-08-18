package com.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.SalesOrder;

public interface SalesOrderRepository
        extends JpaRepository<SalesOrder, Long> {

    boolean existsByDispatchTrackingId(
            String dispatchTrackingId
    );
}