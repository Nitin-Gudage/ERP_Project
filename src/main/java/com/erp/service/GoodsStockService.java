package com.erp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.erp.dto.GoodsStockRequest;
import com.erp.entity.GoodsStock;
import com.erp.entity.Product;
import com.erp.repository.GoodsStockRepository;
import com.erp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoodsStockService {
    private final GoodsStockRepository goodsStockRepository;
    private final ProductRepository productRepository;

    public GoodsStock create(GoodsStockRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        if (goodsStockRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new RuntimeException("Goods stock already exists for product: " + request.getProductId());
        }

        GoodsStock goodsStock = GoodsStock.builder()
                .product(product)
                .availableStock(request.getAvailableStock())
                .build();
        return goodsStockRepository.save(goodsStock);
    }

    public List<GoodsStock> getAll() {
        return goodsStockRepository.findAll();
    }

    public GoodsStock getById(Long id) {
        return goodsStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goods stock not found with id: " + id));
    }

    public GoodsStock getByProductId(Long productId) {
        return goodsStockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Goods stock not found for product: " + productId));
    }

    public GoodsStock update(Long id, GoodsStockRequest request) {
        GoodsStock goodsStock = getById(id);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        goodsStock.setProduct(product);
        goodsStock.setAvailableStock(request.getAvailableStock());
        return goodsStockRepository.save(goodsStock);
    }

    public String delete(Long id) {
        GoodsStock goodsStock = getById(id);
        goodsStockRepository.delete(goodsStock);
        return "Goods stock deleted successfully with id : " + id;
    }
}