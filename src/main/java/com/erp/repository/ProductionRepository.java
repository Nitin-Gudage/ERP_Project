package com.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.Production;

public interface ProductionRepository
        extends JpaRepository<Production, Long> {

}