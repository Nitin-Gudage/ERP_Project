package com.erp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.erp.dto.RawMaterialPerProductRequest;
import com.erp.entity.Product;
import com.erp.entity.RawMaterial;
import com.erp.entity.RawMaterialPerProduct;
import com.erp.repository.ProductRepository;
import com.erp.repository.RawMaterialPerProductRepository;
import com.erp.repository.RawMaterialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawMaterialPerProductService {

	private final RawMaterialPerProductRepository repository;
	private final ProductRepository productRepository;
	private final RawMaterialRepository rawMaterialRepository;

	// Create Raw Material Per Product
	public RawMaterialPerProduct create(RawMaterialPerProductRequest request) {

		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));
		RawMaterial rawMaterial = rawMaterialRepository.findById(request.getRawMaterialId()).orElseThrow(
				() -> new RuntimeException("Raw material not found with id: " + request.getRawMaterialId()));

		RawMaterialPerProduct data = new RawMaterialPerProduct();

		data.setProduct(product);
		data.setRawMaterial(rawMaterial);
		data.setRequiredQuantity(request.getRequiredQuantity());

		return repository.save(data);
	}

	// Get All
	public List<RawMaterialPerProduct> getAll() {
		return repository.findAll();
	}

	// Get By ID
	public RawMaterialPerProduct getById(Long id) {

		return repository.findById(id)
				.orElseThrow(() -> new RuntimeException("Raw material per product not found with id: " + id));
	}

	// Get By Product ID
	public List<RawMaterialPerProduct> getByProductId(Long productId) {
		return repository.findByProductId(productId);
	}

	// Update
	public RawMaterialPerProduct update(Long id, RawMaterialPerProductRequest request) {

		RawMaterialPerProduct existing = getById(id);

		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

		RawMaterial rawMaterial = rawMaterialRepository.findById(request.getRawMaterialId()).orElseThrow(
				() -> new RuntimeException("Raw material not found with id: " + request.getRawMaterialId()));

		existing.setProduct(product);
		existing.setRawMaterial(rawMaterial);
		existing.setRequiredQuantity(request.getRequiredQuantity());
		
		return repository.save(existing);
	}

    public String delete(Long id) {
        RawMaterialPerProduct existing = getById(id);
        repository.delete(existing);
        return "Raw material per product deleted successfully with id : " + id;
    }
}