package com.erp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erp.dto.RawMaterialRequest;
import com.erp.entity.RawMaterial;
import com.erp.repository.RawMaterialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;

    // Create Raw Material
    public RawMaterial create(RawMaterialRequest request) {

        RawMaterial rawMaterial = new RawMaterial();

        rawMaterial.setMaterialCode(request.getMaterialCode());
        rawMaterial.setMaterialName(request.getMaterialName());
        rawMaterial.setAvailableStock(request.getAvailableStock());
        rawMaterial.setDescription(request.getDescription());

        rawMaterial.setIsActive(request.getIsActive() != null ? request.getIsActive() : true
        );

        return rawMaterialRepository.save(rawMaterial);
    }

    // Get All Raw Materials
    public List<RawMaterial> getAll() {

        return rawMaterialRepository.findAll();
    }

    // Get Raw Material By ID
    public RawMaterial getById(Long id) {

        return rawMaterialRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Raw material not found with id: " + id));
    }

    // Update Raw Material
    public RawMaterial update(Long id, RawMaterialRequest request) {

        RawMaterial rawMaterial = getById(id);

        rawMaterial.setMaterialCode(request.getMaterialCode());
        rawMaterial.setMaterialName(request.getMaterialName());
        rawMaterial.setAvailableStock(request.getAvailableStock());
        rawMaterial.setDescription(request.getDescription());

        if (request.getIsActive() != null) {
            rawMaterial.setIsActive(request.getIsActive());
        }

        return rawMaterialRepository.save(rawMaterial);
    }

    public String delete(Long id) {
        RawMaterial rawMaterial = getById(id);
        rawMaterialRepository.delete(rawMaterial);
        return "Raw material deleted successfully with id : " + id;
    }
}