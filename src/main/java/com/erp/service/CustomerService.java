package com.erp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erp.dto.CustomerRequest;
import com.erp.entity.Customer;
import com.erp.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    // Create Customer
    public Customer create(CustomerRequest request) {

        Customer customer = new Customer();

        customer.setCustomerName(request.getCustomerName());
        customer.setContactPerson(request.getContactPerson());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setGstNumber(request.getGstNumber());

        customer.setIsActive(
                request.getIsActive() != null ? request.getIsActive() : true
        );

        return customerRepository.save(customer);
    }

    // Get All Customers
    public List<Customer> getAll() {

        return customerRepository.findAll();
    }

    // Get Customer By ID
    public Customer getById(Long id) {

        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    // Update Customer
    public Customer update(Long id, CustomerRequest request) {

        Customer customer = getById(id);

        customer.setCustomerName(request.getCustomerName());
        customer.setContactPerson(request.getContactPerson());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setGstNumber(request.getGstNumber());

        if (request.getIsActive() != null) {
            customer.setIsActive(request.getIsActive());
        }

        return customerRepository.save(customer);
    }

    public String delete(Long id) {
        Customer customer = getById(id);
        customerRepository.delete(customer);
        return "Customer deleted successfully with id : " + id;
    }
}