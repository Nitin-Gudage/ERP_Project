package com.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

}