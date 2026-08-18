package com.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}