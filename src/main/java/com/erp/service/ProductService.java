package com.erp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.erp.dto.ProductRequest;
import com.erp.entity.Category;
import com.erp.entity.Product;
import com.erp.repository.CategoryRepository;
import com.erp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Product create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));

        Product product = Product.builder()
                .productCode(request.getProductCode())
                .productName(request.getProductName())
                .category(category)
                .sellingPrice(request.getSellingPrice())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        return productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product update(Long id, ProductRequest request) {
        Product existing = getById(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));

        existing.setProductCode(request.getProductCode());
        existing.setProductName(request.getProductName());
        existing.setCategory(category);
        existing.setSellingPrice(request.getSellingPrice());
        existing.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }
        return productRepository.save(existing);
    }

    public String delete(Long id) {
        Product product = getById(id);
        productRepository.delete(product);
        return "Product deleted successfully with id : " + id;
    }
}