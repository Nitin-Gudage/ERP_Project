package com.erp.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.erp.dto.ProductionItemRequest;
import com.erp.entity.Product;
import com.erp.entity.Production;
import com.erp.entity.ProductionItem;
import com.erp.entity.ProductionStatus;
import com.erp.repository.ProductRepository;
import com.erp.repository.ProductionItemRepository;
import com.erp.repository.ProductionRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductionItemService {
    private final ProductionItemRepository itemRepository;
    private final ProductionRepository productionRepository;
    private final ProductRepository productRepository;

    public ProductionItem create(ProductionItemRequest request) {
        Production production = productionRepository.findById(request.getProductionId())
                .orElseThrow(() -> new RuntimeException("Production not found with id: " + request.getProductionId()));

        if (production.getStatus() != ProductionStatus.PENDING) {
            throw new RuntimeException("Items cannot be added after approval");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        ProductionItem item = ProductionItem.builder()
                .production(production)
                .product(product)
                .requiredQuantity(request.getRequiredQuantity())
                .completedQuantity(0)
                .build();
        return itemRepository.save(item);
    }

    public List<ProductionItem> getAll() {
        return itemRepository.findAll();
    }

    public ProductionItem getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Production item not found with id: " + id));
    }

    public List<ProductionItem> getByProductionId(Long productionId) {
        return itemRepository.findByProductionId(productionId);
    }

    public ProductionItem update(Long id, ProductionItemRequest request) {
        ProductionItem item = getById(id);
        Production production = item.getProduction();

        if (production.getStatus() != ProductionStatus.PENDING) {
            throw new RuntimeException("Item cannot be updated after approval");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.getProductId()));

        item.setProduct(product);
        item.setRequiredQuantity(request.getRequiredQuantity());
        return itemRepository.save(item);
    }

    public String delete(Long id) {
        ProductionItem item = getById(id);
        Production production = item.getProduction();

        if (production.getStatus() != ProductionStatus.PENDING) {
            throw new RuntimeException("Item cannot be deleted after approval");
        }
        itemRepository.delete(item);
        return "Production item deleted successfully with id : " + id;
    }
}