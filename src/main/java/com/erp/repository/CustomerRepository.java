package com.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}