package com.erp.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.erp.dto.RawMaterialPurchaseItemRequest;
import com.erp.entity.RawMaterial;
import com.erp.entity.RawMaterialPurchase;
import com.erp.entity.RawMaterialPurchaseItem;
import com.erp.entity.RawMaterialPurchaseStatus;
import com.erp.repository.RawMaterialPurchaseItemRepository;
import com.erp.repository.RawMaterialPurchaseRepository;
import com.erp.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RawMaterialPurchaseItemService {
    private final RawMaterialPurchaseItemRepository itemRepository;
    private final RawMaterialPurchaseRepository purchaseRepository;
    private final RawMaterialRepository rawMaterialRepository;

    @Transactional
    public RawMaterialPurchaseItem create(RawMaterialPurchaseItemRequest request) {
        RawMaterialPurchase purchase = purchaseRepository.findById(request.getPurchaseId())
                .orElseThrow(() -> new RuntimeException("Purchase not found with id: " + request.getPurchaseId()));

        if (purchase.getStatus() != RawMaterialPurchaseStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Items cannot be added after purchase approval");
        }

        RawMaterial rawMaterial = rawMaterialRepository.findById(request.getRawMaterialId())
                .orElseThrow(() -> new RuntimeException("Raw material not found with id: " + request.getRawMaterialId()));

        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        if (request.getUnitPrice() != null && request.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Unit price cannot be negative");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        if (request.getUnitPrice() != null) {
            totalPrice = request.getQuantity().multiply(request.getUnitPrice());
        }

        RawMaterialPurchaseItem item = RawMaterialPurchaseItem.builder()
                .purchase(purchase)
                .rawMaterial(rawMaterial)
                .quantity(request.getQuantity())
                .unitPrice(request.getUnitPrice())
                .totalPrice(totalPrice)
                .build();
        return itemRepository.save(item);
    }

    public List<RawMaterialPurchaseItem> getAll() {
        return itemRepository.findAll();
    }

    public RawMaterialPurchaseItem getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase item not found with id: " + id));
    }

    public List<RawMaterialPurchaseItem> getByPurchaseId(Long purchaseId) {
        purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new RuntimeException("Purchase not found with id: " + purchaseId));
        return itemRepository.findByPurchaseId(purchaseId);
    }

    @Transactional
    public RawMaterialPurchaseItem update(Long id, RawMaterialPurchaseItemRequest request) {
        RawMaterialPurchaseItem item = getById(id);
        RawMaterialPurchase purchase = item.getPurchase();

        if (purchase.getStatus() != RawMaterialPurchaseStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Item cannot be updated after purchase approval");
        }

        RawMaterial rawMaterial = rawMaterialRepository.findById(request.getRawMaterialId())
                .orElseThrow(() -> new RuntimeException("Raw material not found with id: " + request.getRawMaterialId()));

        if (request.getQuantity() == null || request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        if (request.getUnitPrice() != null && request.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Unit price cannot be negative");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        if (request.getUnitPrice() != null) {
            totalPrice = request.getQuantity().multiply(request.getUnitPrice());
        }

        item.setRawMaterial(rawMaterial);
        item.setQuantity(request.getQuantity());
        item.setUnitPrice(request.getUnitPrice());
        item.setTotalPrice(totalPrice);
        return itemRepository.save(item);
    }

    @Transactional
    public String delete(Long id) {
        RawMaterialPurchaseItem item = getById(id);
        RawMaterialPurchase purchase = item.getPurchase();

        if (purchase.getStatus() != RawMaterialPurchaseStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Item cannot be deleted after purchase approval");
        }
        itemRepository.delete(item);
        return "Purchase item deleted successfully with id : " + id;
    }
}