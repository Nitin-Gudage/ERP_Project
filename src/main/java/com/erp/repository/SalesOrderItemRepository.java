package com.erp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.SalesOrderItem;

public interface SalesOrderItemRepository
        extends JpaRepository<SalesOrderItem, Long> {

    List<SalesOrderItem>
    findBySalesOrderId(Long salesOrderId);
}