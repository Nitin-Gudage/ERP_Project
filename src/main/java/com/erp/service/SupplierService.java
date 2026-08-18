package com.erp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.erp.dto.SupplierRequest;
import com.erp.entity.Supplier;
import com.erp.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public Supplier create(SupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .supplierName(request.getSupplierName())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .gstNumber(request.getGstNumber())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        return supplierRepository.save(supplier);
    }

    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    public Supplier getById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    public Supplier update(Long id, SupplierRequest request) {
        Supplier existing = getById(id);
        existing.setSupplierName(request.getSupplierName());
        existing.setContactPerson(request.getContactPerson());
        existing.setPhone(request.getPhone());
        existing.setEmail(request.getEmail());
        existing.setAddress(request.getAddress());
        existing.setGstNumber(request.getGstNumber());
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }
        return supplierRepository.save(existing);
    }

    public String delete(Long id) {
        Supplier supplier = getById(id);
        supplierRepository.delete(supplier);
        return "Supplier deleted successfully with id : " + id;
    }
}